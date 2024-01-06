package model;

import java.util.Objects;

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

    public final Type moveType;

    private int startRow, startCol, endRow, endCol;

    // K: white castles kingside, Q: white castles queenside,
    // k: black castles kingside, q: black castles queenside.
    private char castleType;

    // QRBN for white, qrbn for black
    private char promotionType;

    private boolean isCapture;

    /**
     * Creates a new regular or en passant move
     */
    public Move(int startRow, int startCol, int endRow, int endCol, boolean isEnPassant, boolean isCapture) {
        assert Util.inRange(startRow) && Util.inRange(startCol) && Util.inRange(endRow) && Util.inRange(endCol);
        if (isEnPassant) {
            assert isCapture;
        }
        this.moveType = isEnPassant ? Type.EN_PASSANT : Type.REGULAR;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.isCapture = isCapture;
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
        // Castling can't be a capture
        this.isCapture = false;
    }

    /**
     * Creates a new promotion move. The starting row, ending row and ending column is inferred.
     *
     * @param promotion QRBN for white, qrbn for black
     */
    public Move(int startCol, char promotion, boolean isCapture) {
        assert "QRBNqrbn".indexOf(promotion) >= 0;
        assert Util.inRange(startCol);
        this.moveType = Type.PROMOTION;
        this.startCol = startCol;
        this.endCol = startCol;
        if (promotion >= 'A' && promotion <= 'Z') {
            this.startRow = 6;
            this.endRow = 7;
        } else {
            this.startRow = 1;
            this.endRow = 0;
        }
        this.promotionType = promotion;
        this.isCapture = isCapture;
    }

    /**
     * Creates a move of a special type
     */
    public Move(Type type) {
        assert type == Type.RESIGN || type == Type.OFFER_DRAW
                || type == Type.ACCEPT_DRAW || type == Type.DECLINE_DRAW;
        this.moveType = type;
    }

    public int getStartRow() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION;
        return startRow;
    }

    public int getStartCol() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION;
        return startCol;
    }

    public int getEndRow() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION;
        return endRow;
    }

    public int getEndCol() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION;
        return endCol;
    }

    public char getCastleType() {
        assert moveType == Type.CASTLING;
        return castleType;
    }

    public char getPromotionType() {
        assert moveType == Type.PROMOTION;
        return promotionType;
    }

    public boolean getIsCapture() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION || moveType == Type.CASTLING;
        return isCapture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return startRow == move.startRow && startCol == move.startCol && endRow == move.endRow && endCol == move.endCol && castleType == move.castleType && promotionType == move.promotionType && moveType == move.moveType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveType, startRow, startCol, endRow, endCol, castleType, promotionType);
    }

    @Override
    public String toString() {
        switch (moveType) {
            case CASTLING:
                return (castleType == 'K' || castleType == 'k') ? "O-O" : "O-O-O";
            case RESIGN:
                return "Resign";
            case OFFER_DRAW:
                return "Offer draw";
            case ACCEPT_DRAW:
                return "Accept draw";
            case DECLINE_DRAW:
                return "Decline draw";
            case REGULAR, EN_PASSANT, PROMOTION:
                StringBuilder sb = new StringBuilder();
                sb.append((char)(startCol + 'a'));
                sb.append(startRow + 1);
                sb.append(isCapture ? 'x' : '-');
                sb.append((char)(endCol + 'a'));
                sb.append(endRow + 1);
                if (moveType == Type.PROMOTION) {
                    sb.append('=').append(promotionType);
                }
                return sb.toString();
            default:
                assert false;
                return "";
        }
    }
}
