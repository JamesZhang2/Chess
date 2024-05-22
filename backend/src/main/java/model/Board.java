package model;

import java.util.Set;

public interface Board {
    Board clone();

    /**
     * @return the FEN string representing the current board state
     */
    String toFEN();

    /**
     * @return return the FEN string representing the current board state
     * without the halfMove and fullMove fields
     */
    String getUnclockedFEN();

    /**
     * @return the PGN of this game.
     */
    String toPGN();

    /**
     * @return A readable depiction of the board state, used for debugging
     */
    @Override
    String toString();

    /**
     * If the move is legal, make the move by updating the board state (including the winner) and return true.
     * Otherwise, return false and don't change the board state.
     * <p>
     * Requires: move is of type regular, castling, promotion, or en passant.
     *
     * @return whether the move is legal
     */
    boolean move(Move move);

    /**
     * Undo the last move and restore the board to the same state as the one before the move.
     * If the current board state is already the initial state (when the board was loaded), do nothing.
     *
     * @return false if the board state is already the initial state, true otherwise.
     */
    boolean undoLastMove();

    /**
     * @return the set of legal moves in the current position
     */
    Set<Move> getLegalMoves();

    /**
     * @return the set of legal moves for the piece at position {row, col}.
     * <p>
     * Requires: There is a piece at {row, col} and the color of the piece is the same
     * as the current player
     */
    Set<Move> getLegalMoves(int row, int col);

    /**
     * Only checks whether move is legal or not, does not change the board state.
     *
     * @return whether the move is legal
     */
    boolean isLegal(Move move);

    /**
     * @return true if the side to move is currently in check, false otherwise
     */
    boolean isInCheck();

    /**
     * @return the winner of the game.
     */
    char getWinner();
}
