package model;

/**
 * Represents a chess piece.
 */
public class Piece {
    public enum Type {
        PAWN("P"),
        KNIGHT("N"),
        BISHOP("B"),
        ROOK("R"),
        QUEEN("Q"),
        KING("K");

        final String name;

        Type(String name) {
            this.name = name;
        }
    }

    public final Type type;
    public final boolean isWhite;

    public Piece(Type type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }

    /**
     * Constructs piece from piece name c.
     *
     * @throws IllegalArgumentException if c is not a valid piece name.
     */
    public Piece(char c) {
        if (c >= 'a' && c <= 'z') {
            isWhite = false;
            c = (char) (c - 'a' + 'A');
        } else {
            isWhite = true;
        }
        switch (c) {
            case 'P':
                type = Type.PAWN;
                break;
            case 'N':
                type = Type.KNIGHT;
                break;
            case 'B':
                type = Type.BISHOP;
                break;
            case 'R':
                type = Type.ROOK;
                break;
            case 'Q':
                type = Type.QUEEN;
                break;
            case 'K':
                type = Type.KING;
                break;
            default:
                throw new IllegalArgumentException("Unknown piece: " + (isWhite ? c : c + 'a' - 'A'));
        }
    }

    @Override
    public String toString() {
        return isWhite ? type.name : type.name.toLowerCase();
    }
}
