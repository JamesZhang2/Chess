package model.board;

/**
 * History entries common for both mailbox boards and bitmap boards, used to undo a move.
 */
public record BoardHistoryEntry(boolean whiteCastleK, boolean whiteCastleQ, boolean blackCastleK, boolean blackCastleQ,
                                char enPassantWhite, char enPassantBlack,
                                int halfMove, int fullMove, long zobristHash) {
}
