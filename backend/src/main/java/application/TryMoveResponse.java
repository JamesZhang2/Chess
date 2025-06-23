package application;

/**
 * A class representing a response to a move received from the frontend.
 *
 * @param winner u, w, b, or d
 */
public record TryMoveResponse(boolean isLegal, String fen, String winner) {
}
