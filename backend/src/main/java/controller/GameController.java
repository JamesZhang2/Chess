package controller;

import model.board.BitmapBoard;
import model.board.Board;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import model.player.Action;
import model.player.Player;

/**
 * The controller sets up the board and asks the players to play.
 * Handles resign, offer draw, accept draw, and decline draw.
 */
public class GameController {
    private final Board board;
    private final Player whitePlayer, blackPlayer;

    /**
     * Initialize a game controller with the starting position.
     */
    public GameController(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = new BitmapBoard();
    }

    /**
     * Initialize a game controller with the position specified by the FEN.
     * @throws MalformedFENException if the FEN is malformed
     * @throws IllegalBoardException if the board parsed from the FEN is illegal
     */
    public GameController(Player whitePlayer, Player blackPlayer, String FEN)
            throws IllegalBoardException, MalformedFENException {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = new BitmapBoard(FEN);
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
