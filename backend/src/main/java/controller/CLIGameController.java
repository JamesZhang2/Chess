package controller;

import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import model.player.Action;
import model.player.Player;

/**
 * A Game controller for the CLI.
 */
public class CLIGameController extends GameController {
    public CLIGameController(Player whitePlayer, Player blackPlayer) {
        super(whitePlayer, blackPlayer);
    }

    public CLIGameController(Player whitePlayer, Player blackPlayer, String FEN) throws IllegalBoardException, MalformedFENException {
        super(whitePlayer, blackPlayer, FEN);
    }

    /**
     * Start the game.
     */
    public void startGame() {
        whitePlayer.init(board.clone());
        blackPlayer.init(board.clone());
        // Must pass in a clone of the board (or FEN) to players to avoid tampering with current board state
        while (board.getWinner() == 'u') {
            // Game has not ended
            Player curPlayer = board.whiteToMove() ? whitePlayer : blackPlayer;
            Player otherPlayer = board.whiteToMove() ? blackPlayer : whitePlayer;
            boolean drawDeclined = false;
            boolean moved = false;
            while (!moved) {
                Action action = curPlayer.play(board.clone());
                if (action.getActionType() == Action.Type.RESIGN) {
                    board.resign();
                    moved = true;
                } else if (action.getActionType() == Action.Type.OFFER_DRAW) {
                    // Only ask opponent if the draw has not been declined this turn
                    boolean agreed = !drawDeclined && otherPlayer.considerDraw(board.clone());
                    if (!agreed) {
                        curPlayer.drawDeclined();
                        drawDeclined = true;
                    } else {
                        curPlayer.drawAccepted();
                        board.drawByAgreement();
                        moved = true;
                    }
                } else {
                    board.move(action.getMove());
                    moved = true;
                }
            }
        }
        // end of game
        switch (board.getWinner()) {
            case 'w':
                whitePlayer.win(board.clone());
                blackPlayer.lose(board.clone());
                break;
            case 'd':
                whitePlayer.draw(board.clone());
                blackPlayer.draw(board.clone());
                break;
            case 'b':
                whitePlayer.lose(board.clone());
                blackPlayer.win(board.clone());
                break;
            default:
                assert false;
        }
    }
}
