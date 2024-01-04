/**
 * Represents a chess move (or resign/offer draw).
 */
public class Move {
    public enum Type {
        REGULAR,
        CASTLING,
        EN_PASSANT,
        PROMOTION,
        RESIGN,
        OFFER_DRAW,
        ACCEPT_DRAW,
        DECLINE_DRAW
    }

    Type moveType;

    int startRow, startCol, endRow, endCol;

    // K: white castles kingside, Q: white castles queenside,
    // k: black castles kingside, q: black castles queenside.
    char castleType;

    // QRBN for white, qrbn for black
    char promotionType;

    /**
     * Creates a new regular or en passant move
     */
    public Move(int startRow, int startCol, int endRow, int endCol, boolean isEnPassant) {
        assert Util.inRange(startRow) && Util.inRange(startCol) && Util.inRange(endRow) && Util.inRange(endCol);
        this.moveType = isEnPassant ? Type.EN_PASSANT : Type.REGULAR;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
    }

    /**
     * Creates a new castling move
     *
     * @param castleType K: white castles kingside, Q: white castles queenside, k/q for black
     */
    public Move(char castleType) {
        assert "KQkq".indexOf(castleType) >= 0;
        this.moveType = Type.CASTLING;
        this.castleType = castleType;
    }

    /**
     * Creates a new promotion move. The starting row, ending row and ending column is inferred.
     *
     * @param promotion QRBN for white, qrbn for black
     */
    public Move(int startCol, char promotion) {
        assert "QRBNqrbn".indexOf(promotion) >= 0;
        assert Util.inRange(startCol);
        this.startCol = startCol;
        this.endCol = startCol;
        if (promotion >= 'A' && promotion <= 'Z') {
            this.startRow = 6;
            this.endRow = 7;
        } else {
            this.startRow = 1;
            this.endRow = 0;
        }
    }

    /**
     * Creates a move of a special type
     */
    public Move(Type type) {
        assert type == Type.RESIGN || type == Type.OFFER_DRAW
                || type == Type.ACCEPT_DRAW || type == Type.DECLINE_DRAW;
        this.moveType = type;
    }
}