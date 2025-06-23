package controller;

import application.CandidateMoves;
import application.UIMove;
import application.UIMoveResponse;
import model.Util;
import model.board.Handicap;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import model.move.Move;
import model.player.Action;
import model.player.HumanGUIPlayer;
import model.player.Player;

/**
 * A game controller with additional methods that accept data from and provide data to the frontend
 */
public class GUIGameController extends GameController {
    public GUIGameController(Player whitePlayer, Player blackPlayer) {
        super(whitePlayer, blackPlayer);
    }

    public GUIGameController(Player whitePlayer, Player blackPlayer, Handicap handicap) {
        super(whitePlayer, blackPlayer, handicap);
    }

    public GUIGameController(Player whitePlayer, Player blackPlayer, String FEN) throws IllegalBoardException, MalformedFENException {
        super(whitePlayer, blackPlayer, FEN);
    }

    /**
     * @return the FEN of the current board
     */
    public String getFEN() {
        return board.toFEN();
    }

    /**
     * @param square the square of the piece to generate candidate moves from
     * @return the candidate moves for the piece at the given square
     */
    public CandidateMoves getCandidateMoves(String square) {
        return new CandidateMoves(board, square);
    }

    /**
     * @return the winner of the game. One of 'u' (unknown), 'w' (white), 'd' (draw), 'b' (black).
     */
    public char getWinner() {
        return board.getWinner();
    }

    /**
     * Asks the current player to play a move.
     * Requires: current player is not a HumanGUIPlayer.
     */
    public void playOneMove() {
        Player curPlayer = board.whiteToMove() ? whitePlayer : blackPlayer;
        if (curPlayer instanceof HumanGUIPlayer) {
            throw new IllegalStateException("Can't ask a HumanGUIPlayer to play a move");
        }
        Action action = curPlayer.play(board);
        switch (action.getActionType()) {
            case MOVE -> board.move(action.getMove());
            case RESIGN -> board.resign();
            case OFFER_DRAW -> throw new UnsupportedOperationException();
        }
    }

    /**
     * Try a certain move received from the frontend.
     * If the move is valid, the current player plays the move and the board state is updated.
     * Otherwise, the board state is unchanged.
     *
     * @param uiMove the move received from the frontend
     * @return a UIMoveResponse object indicating whether the move is legal,
     */
    public UIMoveResponse tryMove(UIMove uiMove) {
        CandidateMoves candidates = getCandidateMoves(uiMove.fromSquare);
        Move move = null;
        int[] fromCoords = Util.squareToCoords(uiMove.fromSquare);
        int[] toCoords = Util.squareToCoords(uiMove.toSquare);
        char pieceAtStart = board.getPieceAt(fromCoords[0], fromCoords[1]);
        char pieceAtDest = board.getPieceAt(toCoords[0], toCoords[1]);
        // does not include en passant
        boolean isNormalCapture = pieceAtDest != '0'
                && ((Character.isLowerCase(pieceAtDest) && board.whiteToMove())
                || (Character.isUpperCase(pieceAtDest) && !board.whiteToMove()));
        if (candidates.legalPromotions.contains(uiMove.toSquare)
                && uiMove.promotion != null
                && uiMove.promotion.length() == 1) {
            // promotions
            if (board.whiteToMove() && "QRNB".contains(uiMove.promotion)
                    || !board.whiteToMove() && "qrnb".contains(uiMove.promotion)) {
                move = Util.moveFromSquares(
                        uiMove.fromSquare, uiMove.toSquare, uiMove.promotion.charAt(0), isNormalCapture);
            }
        } else if (candidates.legalDests.contains(uiMove.toSquare)
                && !candidates.legalPromotions.contains(uiMove.toSquare)
                && uiMove.promotion == null) {
            // castling
            if (pieceAtStart == 'K' && fromCoords[0] == 0 && fromCoords[1] == 4 && toCoords[0] == 0 && toCoords[1] == 6) {
                move = new Move('K');
            } else if (pieceAtStart == 'K' && fromCoords[0] == 0 && fromCoords[1] == 4 && toCoords[0] == 0 && toCoords[1] == 2) {
                move = new Move('Q');
            } else if (pieceAtStart == 'k' && fromCoords[0] == 7 && fromCoords[1] == 4 && toCoords[0] == 7 && toCoords[1] == 6) {
                move = new Move('k');
            } else if (pieceAtStart == 'k' && fromCoords[0] == 7 && fromCoords[1] == 4 && toCoords[0] == 7 && toCoords[1] == 2) {
                move = new Move('q');
            } else if ((pieceAtStart == 'P' || pieceAtStart == 'p') && !isNormalCapture && fromCoords[1] != toCoords[1]) {
                // en passant
                move = Util.moveFromSquares(uiMove.fromSquare, uiMove.toSquare, true, true);
            } else {
                // normal move
                move = Util.moveFromSquares(uiMove.fromSquare, uiMove.toSquare, false, isNormalCapture);
            }
        }
        // move is set to the correct move if uiMove is legal, or null if uiMove is not legal
        if (move == null) {
            return new UIMoveResponse(false, board.toFEN(), board.getWinner());
        } else {
            board.move(move);

            // Ask the other player to play if they're not a human GUI player
            // TODO: Move this logic to another function so that the game doesn't freeze when AI is thinking
            if (board.getWinner() == 'u') {
                if ((board.whiteToMove() && !(whitePlayer instanceof HumanGUIPlayer))
                        || (!board.whiteToMove() && !(blackPlayer instanceof HumanGUIPlayer))) {
                    Action action = (board.whiteToMove() ? whitePlayer : blackPlayer).play(board);
                    if (action.getActionType() == Action.Type.MOVE) {
                        board.move(action.getMove());
                    } else {
                        throw new UnsupportedOperationException("Unimplemented");  // TODO
                    }
                }
            }
            return new UIMoveResponse(true, board.toFEN(), board.getWinner());
        }
    }
}
