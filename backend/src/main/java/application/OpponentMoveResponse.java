package application;

/**
 * A response for a waitForOpponent request.
 *
 * @param moved    whether the opponent has moved
 * @param fen      FEN of the current board
 * @param winner   u, w, b, or d
 * @param isResign whether the opponent resigned
 */
public record OpponentMoveResponse(boolean moved, String fen, String winner, boolean isResign) {
}
