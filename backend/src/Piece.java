public class Piece {
    public enum Type {
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING
    }

    private Type type;
    private boolean isWhite;
    public Piece(Type type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }
}
