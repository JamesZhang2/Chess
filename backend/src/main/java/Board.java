import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a legal board state.
 */
public class Board {
    // Representation Invariant: Board state is always legal at the end of a public method.
    // This means that there is one king on both sides, no pawns are on the first or last rank,
    // and the side to move cannot capture the opponent's king.
    // Note that we allow the number of pieces to be arbitrary,
    // and we don't care about whether a position is reachable from the starting position.

    // Representation Invariant: The winner variable is always accurate at the end of a public method.

    // pieces[i][j] is the piece at rank (i + 1), file ('a' + j)
    // For example, pieces[0][0] is the piece at a1, pieces[3][4] is the piece at d3.
    // It is null if there is no piece at that square
    Piece[][] pieces = new Piece[8][8];
    boolean whiteToMove;
    boolean whiteCastleK = false, whiteCastleQ = false, blackCastleK = false, blackCastleQ = false;
    // en passant target squares - for white it is x3, for black it is x6, where x is in [a...h]
    // The value is '-' if there are no en passant target squares for that color.
    char enPassantWhite = '-', enPassantBlack = '-';
    int halfMove;
    int fullMove;

    // w: white, b: black, d: draw, u: unknown
    private char winner;

    private static final char[] PIECE_NAMES = {'p', 'n', 'b', 'r', 'q', 'k', 'P', 'N', 'B', 'R', 'Q', 'K'};

    /**
     * Create board from FEN
     */
    public Board(String fen) throws MalformedFENException, IllegalBoardException {
        parseFen(fen);
        checkBoardLegality();
        updateWinner();
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
     * Requires: The other board is legal
     */
    public Board(Board other) {
        try {
            parseFen(other.toFEN());
            this.winner = other.winner;
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
        StringBuilder sb = new StringBuilder();

        // Piece placement
        for (int r = 7; r >= 0; r--) {
            int blanks = 0;
            for (int c = 0; c < 8; c++) {
                if (pieces[r][c] == null) {
                    blanks++;
                } else {
                    if (blanks > 0) {
                        sb.append(blanks);
                    }
                    blanks = 0;
                    sb.append(pieces[r][c]);
                }
            }
            if (blanks > 0) {
                sb.append(blanks);
            }
            sb.append('/');
        }
        // Remove last slash
        sb.delete(sb.length() - 1, sb.length());

        // Active color
        sb.append(' ');
        sb.append(whiteToMove ? 'w' : 'b');

        // Castling
        sb.append(' ');
        if (!whiteCastleK && !whiteCastleQ && !blackCastleK && !blackCastleQ) {
            sb.append('-');
        } else {
            if (whiteCastleK) {
                sb.append('K');
            }
            if (whiteCastleQ) {
                sb.append('Q');
            }
            if (blackCastleK) {
                sb.append('k');
            }
            if (blackCastleQ) {
                sb.append('q');
            }
        }

        // En passant
        sb.append(' ');
        if (enPassantWhite == '-' && enPassantBlack == '-') {
            sb.append('-');
        } else {
            if (enPassantBlack == '-') {
                sb.append(enPassantWhite);
                sb.append(3);
            } else {
                sb.append(enPassantBlack);
                sb.append(6);
            }
        }

        // Halfmove
        sb.append(' ');
        sb.append(halfMove);

        // Fullmove
        sb.append(' ');
        sb.append(fullMove);

        return sb.toString();
    }

    /**
     * @return A readable depiction of the board state, used for debugging
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 7; r >= 0; r--) {
            for (int c = 0; c < 8; c++) {
                sb.append(pieces[r][c] == null ? '.' : pieces[r][c]);
            }
            sb.append("\n");
        }

        if (whiteToMove) {
            sb.append("White to move\n");
        } else {
            sb.append("Black to move\n");
        }

        sb.append("White O-O: ").append(whiteCastleK).append("\n");
        sb.append("White O-O-O: ").append(whiteCastleQ).append("\n");
        sb.append("Black O-O: ").append(blackCastleK).append("\n");
        sb.append("Black O-O-O: ").append(blackCastleQ).append("\n");

        sb.append("White en passant target square: ").append(enPassantWhite == '-' ? '-' : enPassantWhite + "3");
        sb.append("\n");
        sb.append("Black en passant target square: ").append(enPassantBlack == '-' ? '-' : enPassantBlack + "6");
        sb.append("\n");

        sb.append("Halfmove clock: ").append(halfMove).append("\n");
        sb.append("Fullmove number: ").append(fullMove).append("\n");

        return sb.toString();
    }

    /**
     * Parses the FEN string and updates the board states accordingly.
     * Note that the resulting board state may be illegal.
     *
     * @throws MalformedFENException if the FEN is malformed.
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

        // En passant
        String enPassant = fields[3];
        if (enPassant.length() == 1) {
            if (enPassant.charAt(0) != '-') {
                throw new MalformedFENException("Malformed en passant field: " + enPassant);
            }
        } else if (enPassant.length() == 2) {
            if (enPassant.charAt(0) < 'a' || enPassant.charAt(0) > 'h') {
                throw new MalformedFENException("Unknown file in en passant field: " + enPassant.charAt(0));
            }

            if (enPassant.charAt(1) == '3') {
                enPassantWhite = enPassant.charAt(0);
            } else if (enPassant.charAt(1) == '6') {
                enPassantBlack = enPassant.charAt(0);
            } else {
                throw new MalformedFENException("Rank in en passant field must be 3 or 6");
            }
        } else {
            throw new MalformedFENException("Malformed en passant field: " + enPassant);
        }

        // Halfmove
        try {
            halfMove = Integer.parseInt(fields[4]);
            if (halfMove < 0) {
                throw new MalformedFENException("Halfmove field is negative: " + halfMove);
            }
        } catch (NumberFormatException e) {
            throw new MalformedFENException("Halfmove field is not an integer: " + fields[4]);
        }

        // Fullmove
        try {
            fullMove = Integer.parseInt(fields[5]);
            if (fullMove <= 0) {
                throw new MalformedFENException("Fullmove field is not positive: " + fullMove);
            }
        } catch (NumberFormatException e) {
            throw new MalformedFENException("Fullmove field is not an integer: " + fields[5]);
        }
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
     * Checks whether the board state is legal.
     *
     * @throws IllegalBoardException if the board state is illegal.
     */
    private void checkBoardLegality() throws IllegalBoardException {
        boolean whiteKing = false;
        boolean blackKing = false;

        // Check number of kings
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (pieces[r][c] != null) {
                    if (pieces[r][c].toString().equals("K")) {
                        if (whiteKing) {
                            throw new IllegalBoardException("More than one white kings on the board");
                        }
                        whiteKing = true;
                    } else if (pieces[r][c].toString().equals("k")) {
                        if (blackKing) {
                            throw new IllegalBoardException("More than one black kings on the board");
                        }
                        blackKing = true;
                    }
                }
            }
        }

        if (!whiteKing) {
            System.out.println(this);
            throw new IllegalBoardException("No white kings on the board");
        }
        if (!blackKing) {
            throw new IllegalBoardException("No black kings on the board");
        }

        // Check pawns on first or last rank
        for (int c = 0; c < 8; c++) {
            if (pieces[0][c] != null && pieces[0][c].type == Piece.Type.PAWN) {
                throw new IllegalBoardException("There is a pawn on rank 1");
            }
            if (pieces[7][c] != null && pieces[7][c].type == Piece.Type.PAWN) {
                throw new IllegalBoardException("There is a pawn on rank 8");
            }
        }

        // Check whether the player not playing is in check
        if (isInCheck(!whiteToMove)) {
            String opponent = whiteToMove ? "Black" : "White";
            throw new IllegalBoardException(opponent + " is in check but it's not their move");
        }
    }

    /**
     * If the move is legal, make the move by updating the board state and return true.
     * Otherwise, return false and don't change the board state.
     * Requires: move is of type regular, castling, promotion, or en passant.
     *
     * @return whether the move is legal
     */
    public boolean move(Move move) {
        // TODO
        assert move.moveType == Move.Type.REGULAR || move.moveType == Move.Type.CASTLING
                || move.moveType == Move.Type.EN_PASSANT || move.moveType == Move.Type.PROMOTION;
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
        Set<List<Integer>> ans = new HashSet<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] != null && pieces[row][col].isWhite == white) {
                    Set<List<Integer>> pieceControls = controls(row, col);
                    ans.addAll(pieceControls);
                }
            }
        }
        return ans;
    }

    /**
     * @return the set of squares that is controlled by the piece at {row, col}
     * Requires: there exists a piece at {row, col}
     */
    private Set<List<Integer>> controls(int row, int col) {
        assert pieces[row][col] != null;
        Set<List<Integer>> ans = new HashSet<>();

        int[][] dirs = switch (pieces[row][col].type) {
            case QUEEN, KING -> new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            case ROOK -> new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            case BISHOP -> new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            case KNIGHT -> new int[][]{{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
            case PAWN -> pieces[row][col].isWhite ? new int[][]{{1, 1}, {1, -1}} : new int[][]{{-1, 1}, {-1, -1}};
        };

        switch (pieces[row][col].type) {
            case QUEEN, ROOK, BISHOP:
                // Infinite range
                for (int[] dir : dirs) {
                    int r = row + dir[0];
                    int c = col + dir[1];
                    while (Util.inRange(r) && Util.inRange(c)) {
                        List<Integer> sqr = new ArrayList<>();
                        sqr.add(r);
                        sqr.add(c);
                        ans.add(sqr);
                        if (pieces[r][c] != null) {
                            // Hit an obstacle (note that this square is still controlled)
                            break;
                        }
                        r += dir[0];
                        c += dir[1];
                    }
                }
                break;
            case KING, KNIGHT, PAWN:
                // One-step range
                for (int[] dir : dirs) {
                    int r = row + dir[0];
                    int c = col + dir[1];
                    if (Util.inRange(r) && Util.inRange(c)) {
                        List<Integer> sqr = new ArrayList<>();
                        sqr.add(r);
                        sqr.add(c);
                        ans.add(sqr);
                    }
                }
                break;
        }
        return ans;
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
    public Set<Move> getLegalMoves(int row, int col) {
        // TODO
        return new HashSet<>();
    }

    /**
     * Only checks whether move is legal or not, does not change the board state.
     * Requires: move is of type regular, castle, or en passant.
     *
     * @return whether the move is legal
     */
    public boolean isLegal(Move move) {
        // TODO
        assert move.moveType == Move.Type.REGULAR || move.moveType == Move.Type.CASTLING
                || move.moveType == Move.Type.EN_PASSANT;
        return false;
    }

    /**
     * @return true if the side to move is currently in check, false otherwise
     */
    public boolean isInCheck() {
        return isInCheck(whiteToMove);
    }

    /**
     * @return true if white/black is in check (determined by the parameter white), false otherwise
     */
    private boolean isInCheck(boolean white) {
        List<Integer> kingPos = getKingPos(white);
        return controls(!white).contains(kingPos);
    }

    /**
     * @return the position of the white/black king (determined by the parameter white)
     * Since we assume the rep invariant is true, there is only one king of each color.
     */
    private List<Integer> getKingPos(boolean white) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (pieces[r][c] != null && pieces[r][c].type == Piece.Type.KING && pieces[r][c].isWhite == white) {
                    List<Integer> ans = new ArrayList<>();
                    ans.add(r);
                    ans.add(c);
                    return ans;
                }
            }
        }
        assert false;
        return new ArrayList<>();
    }

    /**
     * @return the winner of the game.
     */
    public char getWinner() {
        return winner;
    }

    /**
     * Check if the game ended and update the winner variable.
     */
    private void updateWinner() {
        // TODO
    }
}
