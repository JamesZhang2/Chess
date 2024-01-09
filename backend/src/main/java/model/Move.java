package model;

import java.util.Objects;

/**
 * Represents a chess move (or resign, offer draw, accept draw, or decline draw).
 * Note that this is not used in the PGN because the Standard Algebra Notation (SAN) in the PGN
 * requires more information about the board (like checks, checkmates, and avoiding ambiguity)
 */
public class Move {
    public enum Type {
        REGULAR,
        CASTLING,
        EN_PASSANT,
        PROMOTION,
        // TODO: Consider removing the following as moves, and use separate methods to handle them
        // One reason that they shouldn't be moves is that players should be allowed to resign/offer draw at any time
        RESIGN,
        OFFER_DRAW,
        ACCEPT_DRAW,
        DECLINE_DRAW
    }

    public final Type moveType;

    // For castling, these fields are the starting and ending positions of the king.
    // For resign and draw, these fields are -1, and they should never be queried.
    private final int startRow, startCol, endRow, endCol;

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
        if (castleType == 'K' || castleType == 'Q') {
            this.startRow = 0;
            this.endRow = 0;
        } else {
            this.startRow = 7;
            this.endRow = 7;
        }
        this.startCol = 4;
        if (castleType == 'K' || castleType == 'k') {
            this.endCol = 6;
        } else {
            this.endCol = 2;
        }
        this.moveType = Type.CASTLING;
        this.castleType = castleType;
        // Castling can't be a capture
        this.isCapture = false;
    }

    /**
     * Creates a new promotion move.
     *
     * @param promotion QRBN for white, qrbn for black
     */
    public Move(int startRow, int startCol, int endRow, int endCol, char promotion, boolean isCapture) {
        assert "QRBNqrbn".indexOf(promotion) >= 0;
        assert Util.inRange(startCol);
        this.moveType = Type.PROMOTION;
        this.startCol = startCol;
        this.endCol = endCol;
        if (promotion >= 'A' && promotion <= 'Z') {
            assert startRow == 6 && endRow == 7;
            this.startRow = 6;
            this.endRow = 7;
        } else {
            assert startRow == 1 && endRow == 0;
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
        startRow = startCol = endRow = endCol = -1;
        this.moveType = type;
    }

    public int getStartRow() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION || moveType == Type.CASTLING;
        return startRow;
    }

    public int getStartCol() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION || moveType == Type.CASTLING;
        return startCol;
    }

    public int getEndRow() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION || moveType == Type.CASTLING;
        return endRow;
    }

    public int getEndCol() {
        assert moveType == Type.REGULAR || moveType == Type.EN_PASSANT || moveType == Type.PROMOTION || moveType == Type.CASTLING;
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
        return startRow == move.startRow && startCol == move.startCol && endRow == move.endRow && endCol == move.endCol
                && castleType == move.castleType && promotionType == move.promotionType && moveType == move.moveType;
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
