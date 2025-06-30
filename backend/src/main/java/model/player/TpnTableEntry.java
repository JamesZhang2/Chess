package model.player;

import model.move.Move;

/**
 *
 * An entry in a transposition table.
 * @param zobristHash the Zobrist hash for this entry
 * @param type either exact, lower bound, or upper bound
 * @param eval the evaluation (if exact), or the bound (if lower bound or upper bound)
 * @param depth the depth of the search used to get this entry.
 *              We should only use this entry if the current search depth is <= the depth of this entry.
 * @param bestMove the best move from the current position, or null if the best move is not computed in the search.
 */
public record TpnTableEntry(long zobristHash, TpnTableEntry.Type type, double eval, int depth, Move bestMove) {
    public enum Type {
        EXACT,
        LOWER_BOUND,
        UPPER_BOUND
    }
}
