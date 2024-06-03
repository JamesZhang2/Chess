package model.move;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chess game by Portable Game Notation (PGN).
 */
public class PGN {
    private final int numStart;  // The starting move number
    private final boolean whiteStart;  // true if the first move is made by white, false otherwise

    private final List<String> moves;  // Moves are in Standard Algebraic Notation (SAN)

    private String result;  // 1-0, 1/2-1/2, 0-1, or *

    public PGN(int numStart, boolean whiteStart, String result) {
        this.numStart = numStart;
        this.whiteStart = whiteStart;
        this.moves = new ArrayList<>();
        this.result = result;
    }

    public PGN(PGN other) {
        this.numStart = other.numStart;
        this.whiteStart = other.whiteStart;
        this.moves = new ArrayList<>(other.moves);
        this.result = other.result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int curMove = numStart;
        boolean whiteToMove = whiteStart;
        if (!whiteToMove) {
            sb.append(numStart).append("... ");
        }
        for (String move : moves) {
            if (whiteToMove) {
                sb.append(curMove++).append(". ");
            }
            sb.append(move).append(" ");
        }
        if (!result.equals("*")) {
            sb.append(result);
        }
        return sb.toString();
    }

    /**
     * Adds a new move to the PGN
     * @param move the move to be added
     */
    public void addMove(String move) {
        moves.add(move);
    }

    /**
     * Undo the last move if there is at least one move in the PGN, do nothing otherwise.
     * @return the move that was undone, or null if there is no move in the PGN.
     */
    public String undoLastMove() {
        if (moves.isEmpty()) {
            return null;
        }
        return moves.removeLast();
    }

    /**
     * Sets the result field of the PGN.
     * @param result the result to be set
     */
    public void setResult(String result) {
        this.result = result;
    }
}
