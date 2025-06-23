package application;

/**
 * A class representing a move received from the frontend. May be illegal.
 *
 * @param fromSquare name of the starting square
 * @param toSquare   name of the destination square
 * @param promotion  single letter representing a piece, or null if not a promotion
 */
public record UIMove(String fromSquare, String toSquare, String promotion) {

    @Override
    public String toString() {
        if (promotion == null) {
            return fromSquare + "-" + toSquare;
        } else {
            return fromSquare + "-" + toSquare + "=" + promotion;
        }
    }
}
