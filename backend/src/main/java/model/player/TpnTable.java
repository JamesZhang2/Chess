package model.player;

import model.move.Move;

/**
 * A class representing a transposition table.
 * Abstractly, a transposition table maps positions to evaluations.
 * For our implementation, we map Zobrist hashes to TpnTable.Entry.
 * We use a fixed-size array of entries so that the table doesn't grow indefinitely.
 */
public class TpnTable {
    public final int SIZE;
    private final Entry[] entries;

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
    public record Entry(long zobristHash, Type type, double eval, int depth, Move bestMove) {
        public enum Type {
            EXACT,
            LOWER_BOUND,
            UPPER_BOUND
        }
    }

    public TpnTable(int size) {
        this.SIZE = size;
        this.entries = new Entry[SIZE];
    }

    /**
     * @param zobristHash the Zobrist hash to query
     * @return the entry if the corresponding entry is found, null otherwise.
     */
    public Entry get(long zobristHash) {
        int idx = (int)(zobristHash % SIZE + SIZE) % SIZE;
        if (entries[idx] == null) {
            return null;
        } else {
            if (entries[idx].zobristHash == zobristHash) {
                return entries[idx];
            } else {
                return null;
            }
        }
    }

    /**
     * If the bucket already has an entry,
     * we only replace if the depth of this entry is strictly greater
     * than the depth of the existing entry.
     * @return true if the entry is put in the table, false otherwise.
     */
    public boolean put(long zobristHash, Entry entry) {
        int idx = (int)(zobristHash % SIZE + SIZE) % SIZE;
        if (entries[idx] == null) {
            entries[idx] = entry;
            return true;
        } else {
            if (entry.depth > entries[idx].depth) {
                entries[idx] = entry;
                return true;
            } else {
                return false;
            }
        }
    }
}
