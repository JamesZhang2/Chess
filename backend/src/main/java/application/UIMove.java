package application;

/**
 * A class representing a move received from the frontend. May be illegal.
 */
public class UIMove {
    private final String fromSquare;
    private final String toSquare;
    private final String promotePiece;

    public UIMove(String fromSquare, String toSquare, String promotePiece) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.promotePiece = promotePiece;
    }

    @Override
    public String toString() {
        if (promotePiece == null) {
            return fromSquare + "-" + toSquare;
        } else {
            return fromSquare + "-" + toSquare + "=" + promotePiece;
        }
    }
}
