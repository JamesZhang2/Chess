/**
 * Represents a chess move (or resign/offer draw).
 */
public class Move {
    public enum Type {
        REGULAR,
        CASTLE,
        EN_PASSANT,
        RESIGN,
        OFFER_DRAW,
        ACCEPT_DRAW,
        DECLINE_DRAW
    }

    Type moveType;

    char startFile, endFile;
    int startRank, endRank;

    // K: white castles kingside, Q: white castles queenside,
    // k: black castles kingside, q: black castles queenside.
    char castleType;

    /**
     *
     * @param startFile
     * @param startRank
     * @param endFile
     * @param endRank
     * @param isEnPassant
     */
    public Move(char startFile, int startRank, char endFile, int endRank, boolean isEnPassant) {
        this.moveType = isEnPassant ? Type.EN_PASSANT : Type.REGULAR;
        this.startFile = startFile;
        this.endFile = endFile;
        this.startRank = startRank;
        this.endRank = endRank;
    }

    public Move(char castleType) {
        this.moveType = Type.CASTLE;
        this.castleType = castleType;
    }

    public Move(Type type) {
        assert type == Type.RESIGN || type == Type.OFFER_DRAW
                || type == Type.ACCEPT_DRAW || type == Type.DECLINE_DRAW;
        this.moveType = type;
    }
}
