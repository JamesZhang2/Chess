package controller;

import application.CandidateMoves;
import application.UIMove;
import application.TryMoveResponse;
import application.OpponentMoveResponse;
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
    private final String whiteName;
    private final String blackName;
    public GUIGameController(String whiteName, Player whitePlayer, String blackName, Player blackPlayer) {
        super(whitePlayer, blackPlayer);
        this.whiteName = whiteName;
        this.blackName = blackName;
    }

    public GUIGameController(String whiteName, Player whitePlayer, String blackName, Player blackPlayer, Handicap handicap) {
        super(whitePlayer, blackPlayer, handicap);
        this.whiteName = whiteName;
        this.blackName = blackName;
    }

    public GUIGameController(String whiteName, Player whitePlayer, String blackName, Player blackPlayer, String FEN) throws IllegalBoardException, MalformedFENException {
        super(whitePlayer, blackPlayer, FEN);
        this.whiteName = whiteName;
        this.blackName = blackName;
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

    public String getWhiteName() {
        return whiteName;
    }

    public String getBlackName() {
        return blackName;
    }

    /**
     * Asks the current player to play a move.
     * If the current player is a HumanGUIPlayer, do nothing.
     * If the game is over, do nothing.
     */
    public OpponentMoveResponse playOneMove() {
        Player curPlayer = board.whiteToMove() ? whitePlayer : blackPlayer;
        if (board.getWinner() == 'u' && !(curPlayer instanceof HumanGUIPlayer)) {
            Action action = curPlayer.play(board);
            if (action.getActionType() == Action.Type.MOVE) {
                board.move(action.getMove());
                return new OpponentMoveResponse(board.toFEN(), String.valueOf(board.getWinner()), false);
            } else if (action.getActionType() == Action.Type.RESIGN) {
                board.resign();
                return new OpponentMoveResponse(board.toFEN(), String.valueOf(board.getWinner()), true);
            } else {
                throw new UnsupportedOperationException("Unimplemented");  // TODO
            }
        }
        return new OpponentMoveResponse(board.toFEN(), String.valueOf(board.getWinner()), false);
    }

    /**
     * Try a certain move received from the frontend.
     * If the move is valid, the current player plays the move and the board state is updated.
     * Otherwise, the board state is unchanged.
     *
     * @param uiMove the move received from the frontend
     * @return a UIMoveResponse object indicating whether the move is legal,
     */
    public TryMoveResponse tryMove(UIMove uiMove) {
        CandidateMoves candidates = getCandidateMoves(uiMove.fromSquare());
        Move move = null;
        int[] fromCoords = Util.squareToCoords(uiMove.fromSquare());
        int[] toCoords = Util.squareToCoords(uiMove.toSquare());
        char pieceAtStart = board.getPieceAt(fromCoords[0], fromCoords[1]);
        char pieceAtDest = board.getPieceAt(toCoords[0], toCoords[1]);
        // does not include en passant
        boolean isNormalCapture = pieceAtDest != '0'
                && ((Character.isLowerCase(pieceAtDest) && board.whiteToMove())
                || (Character.isUpperCase(pieceAtDest) && !board.whiteToMove()));
        if (candidates.legalPromotions.contains(uiMove.toSquare())
                && uiMove.promotion() != null
                && uiMove.promotion().length() == 1) {
            // promotions
            if (board.whiteToMove() && "QRNB".contains(uiMove.promotion())
                    || !board.whiteToMove() && "qrnb".contains(uiMove.promotion())) {
                move = Util.moveFromSquares(
                        uiMove.fromSquare(), uiMove.toSquare(), uiMove.promotion().charAt(0), isNormalCapture);
            }
        } else if (candidates.legalDests.contains(uiMove.toSquare())
                && !candidates.legalPromotions.contains(uiMove.toSquare())
                && uiMove.promotion() == null) {
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
                move = Util.moveFromSquares(uiMove.fromSquare(), uiMove.toSquare(), true, true);
            } else {
                // normal move
                move = Util.moveFromSquares(uiMove.fromSquare(), uiMove.toSquare(), false, isNormalCapture);
            }
        }
        // move is set to the correct move if uiMove is legal, or null if uiMove is not legal
        if (move == null) {
            return new TryMoveResponse(false, board.toFEN(), String.valueOf(board.getWinner()));
        } else {
            board.move(move);
            return new TryMoveResponse(true, board.toFEN(), String.valueOf(board.getWinner()));
        }
    }
}
