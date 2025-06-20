package application;

/**
 * A class representing a move received from the frontend. May be illegal.
 */
public class UIMove {
    // making the fields public final since this is like a record
    public final String fromSquare;
    public final String toSquare;
    public final String promotion;  // null if not a promotion

    public UIMove(String fromSquare, String toSquare, String promotion) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        if (promotion == null) {
            return fromSquare + "-" + toSquare;
        } else {
            return fromSquare + "-" + toSquare + "=" + promotion;
        }
    }
}
