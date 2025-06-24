package application;

import model.Util;
import model.board.Board;
import model.move.Move;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class representing candidate moves (legal destination squares & promotion squares) of a piece
 */
public class CandidateMoves {
    public final List<String> legalDests;
    public final List<String> legalPromotions;

    /**
     * Construct CandidateMoves based on the board and the starting piece location
     * If the starting square is not a familiar piece (i.e. empty or enemy piece),
     * or if there are no legal moves for the piece,
     * both legalDestSquares and legalPromotionSquares are initialized to the empty list.
     * @param board current board
     * @param startingSquare the starting square of the piece
     */
    public CandidateMoves(Board board, String startingSquare) {
        int[] coords = Util.squareToCoords(startingSquare);
        int r = coords[0];
        int c = coords[1];
        if (board.getPieceAt(r, c) == 0
            || (Character.isLowerCase(board.getPieceAt(r, c)) && board.whiteToMove())
            || (Character.isUpperCase(board.getPieceAt(r, c)) && !board.whiteToMove())) {
            legalDests = new ArrayList<>();
            legalPromotions = new ArrayList<>();
            return;
        }
        Set<String> legalDestSet = new HashSet<>();
        Set<String> legalPromotionSet = new HashSet<>();
        Set<Move> moves = board.getLegalMoves(r, c);
        for (Move move : moves) {
            String square = Util.coordsToSquare(move.getEndRow(), move.getEndCol());
            legalDestSet.add(square);
            if (move.moveType == Move.Type.PROMOTION) {
                legalPromotionSet.add(square);
            }
        }
        legalDests = new ArrayList<>(legalDestSet);
        legalPromotions = new ArrayList<>(legalPromotionSet);
    }
}
