package model.board;

/**
 * History entries common for both mailbox boards and bitmap boards, used to undo a move.
 */
public record BoardHistoryEntry(boolean whiteCastleK, boolean whiteCastleQ, boolean blackCastleK, boolean blackCastleQ,
                                char enPassantWhite, char enPassantBlack,
                                int halfMove, int fullMove, long zobristHash) {
    @Override
    public String toString() {
        return "BoardHistoryEntry{" +
                "whiteCastleK=" + whiteCastleK +
                ", whiteCastleQ=" + whiteCastleQ +
                ", blackCastleK=" + blackCastleK +
                ", blackCastleQ=" + blackCastleQ +
                ", enPassantWhite=" + enPassantWhite +
                ", enPassantBlack=" + enPassantBlack +
                ", halfMove=" + halfMove +
                ", fullMove=" + fullMove +
                ", zobristHash=" + zobristHash +
                '}';
    }
}
