package model;

import java.util.Set;

/**
 * Represents a legal board state using the bitmap (or bitboard) representation.
 * See <a href="https://www.chessprogramming.org/Bitboards">chess programming wiki</a>
 * for more details about Bitboards.
 */
public class BitmapBoard implements Board{
    @Override
    public Board clone() {
        return null;
    }

    @Override
    public String toFEN() {
        return null;
    }

    @Override
    public String getUnclockedFEN() {
        return null;
    }

    @Override
    public String toPGN() {
        return null;
    }

    @Override
    public boolean move(Move move) {
        return false;
    }

    @Override
    public boolean undoLastMove() {
        return false;
    }

    @Override
    public Set<Move> getLegalMoves() {
        return null;
    }

    @Override
    public Set<Move> getLegalMoves(int row, int col) {
        return null;
    }

    @Override
    public boolean isLegal(Move move) {
        return false;
    }

    @Override
    public boolean isInCheck() {
        return false;
    }

    @Override
    public char getWinner() {
        return 0;
    }
}
