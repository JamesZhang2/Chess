import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a legal board state.
 */
public class Board {
    // pieces[i][j] is the piece at rank (i + 1), file ('a' + j)
    // For example, pieces[0][0] is the piece at a1, pieces[3][4] is the piece at d3.
    Piece[][] pieces = new Piece[8][8];
    boolean whiteToMove;
    boolean whiteCastleK = false, whiteCastleQ = false, blackCastleK = false, blackCastleQ = false;
    // en passant target squares - for white it is x3, for black it is x6, where x is in [a...h]
    char enPassantWhite;
    char enPassantBlack;
    int halfMove;
    int fullMove;

    private static final char[] PIECE_NAMES = {'p', 'n', 'b', 'r', 'q', 'k', 'P', 'N', 'B', 'R', 'Q', 'K'};

    /**
     * Create board from FEN
     */
    public Board(String fen) throws MalformedFENException, IllegalBoardException {
        // TODO
    }

    /**
     * Starting position
     */
    public Board() {
        try {
            parseFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        } catch (Exception e) {
            assert false;
        }
    }

    /**
     * Creates a clone of the other board
     */
    public Board(Board other) {
        try {
            parseFen(other.toFEN());
        } catch (Exception e) {
            assert false;
        }
    }

    @Override
    public Board clone() {
        return new Board(this);
    }

    /**
     * @return the FEN string representing the current board state
     */
    public String toFEN() {
        // TODO
        return "";
    }

    @Override
    public String toString() {
        // TODO
        return "";
    }

    /**
     * Parses the FEN string and updates the board states accordingly.
     *
     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
     */
    private void parseFen(String fen) throws MalformedFENException {
        fen = fen.strip();
        String[] fields = fen.split(" ");
        if (fields.length != 6) {
            throw new MalformedFENException("Number of fields in FEN is not 6");
        }

        // Piece placement
        String[] placement = fields[0].split("/");
        parsePiecePlacement(placement);

        // Active color
        if (fields[1].length() != 1) {
            throw new MalformedFENException("Active color field does not have length 1");
        }
        if (fields[1].charAt(0) == 'w') {
            whiteToMove = true;
        } else if (fields[1].charAt(0) == 'b') {
            whiteToMove = false;
        } else {
            throw new MalformedFENException("Unknown character in active color field: " + fields[1].charAt(0));
        }

        // Castling
        if (!(fields[2].length() == 1 && fields[2].charAt(0) == '-')) {
            Set<Character> seen = new HashSet<>();
            for (int i = 0; i < fields[2].length(); i++) {
                char c = fields[2].charAt(i);
                if (seen.contains(c)) {
                    throw new MalformedFENException("Duplicate character in castling field: " + c);
                }
                seen.add(c);
                switch (fields[2].charAt(i)) {
                    case 'K':
                        whiteCastleK = true;
                        break;
                    case 'Q':
                        whiteCastleQ = true;
                        break;
                    case 'k':
                        blackCastleK = true;
                        break;
                    case 'q':
                        blackCastleQ = true;
                        break;
                    default:
                        throw new MalformedFENException("Unknown character in castling field: " + fields[2].charAt(i));
                }
            }
        }

        // TODO
    }

    /**
     * Parses the piece placement array and updates the pieces variable.
     *
     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
     */
    private void parsePiecePlacement(String[] placement) throws MalformedFENException {
        if (placement.length != 8) {
            throw new MalformedFENException("Number of rows in piece placement field is not 8");
        }
        for (int row = 0; row < 8; row++) {
            // row is the index of the row in the FEN string
            // row 0 corresponds to rank 8, which is index 7 for pieces
            String rowStr = placement[7 - row];
            int col = 0;
            for (int i = 0; i < rowStr.length(); i++) {
                if (col >= 8) {
                    throw new MalformedFENException("Number of pieces and blanks in rank "
                            + (8 - row) + " is greater than 8");
                }
                char curChar = rowStr.charAt(i);
                if (curChar >= '1' && curChar <= '8') {
                    // curChar represents a series of blanks
                    int blanks = curChar - '0';
                    if (col + blanks > 8) {
                        throw new MalformedFENException("Number of pieces and blanks in rank "
                                + (8 - row) + " is greater than 8");
                    }
                    for (int j = 0; j < blanks; j++) {
                        pieces[row][col++] = null;
                    }
                } else {
                    // curChar may represent a piece
                    boolean found = false;
                    for (char valid : PIECE_NAMES) {
                        if (curChar == valid) {
                            pieces[row][col++] = new Piece(curChar);
                            found = true;
                        }
                    }
                    if (!found) {
                        throw new MalformedFENException("Unknown piece name: " + curChar);
                    }
                }
            }
            if (col != 8) {
                throw new MalformedFENException("Number of pieces and blanks in rank "
                        + (8 - row) + " is less than 8");
            }
        }
    }

    /**
     * If the move is legal, make the move by updating the board state and return true.
     * Otherwise, return false and don't change the board state.
     * Requires: move is of type regular, castle, or en passant.
     * @return whether the move is legal
     */
    public boolean move(Move move) {
        // TODO
        assert move.moveType == Move.Type.REGULAR || move.moveType == Move.Type.CASTLE
                || move.moveType == Move.Type.EN_PASSANT;
        return false;
    }

    /**
     * @return the set of squares that white or black controls.
     * The format of a square is consistent with the format of the pieces variable,
     * that is, {i, j} means rank (i + 1), file ('a' + j).
     * A square is said to be "controlled" by white if putting a black king there would result in
     * the black king being in check.
     */
    private Set<List<Integer>> controls(boolean white) {
        // TODO
        return new HashSet<>();
    }

    /**
     * @return the set of legal moves in the current position
     */
    public Set<Move> getLegalMoves() {
        // TODO
        return new HashSet<>();
    }

    /**
     * @return the set of legal moves for the piece at position {row, col}.
     */
    private Set<Move> getLegalMoves(int row, int col) {
        // TODO
        return new HashSet<>();
    }

    /**
     * Only checks whether move is legal or not, does not change the board state.
     * Requires: move is of type regular, castle, or en passant.
     * @return whether the move is legal
     */
    public boolean isLegal(Move move) {
        // TODO
        assert move.moveType == Move.Type.REGULAR || move.moveType == Move.Type.CASTLE
                || move.moveType == Move.Type.EN_PASSANT;
        return false;
    }
}
