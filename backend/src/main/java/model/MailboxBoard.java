package model;

import java.util.*;

/**
 * Represents a legal board state using the mailbox representation.
 * The pieces on the board are represented by a 2D char array.
 * The word "Mailbox" comes from <a href="https://www.chessprogramming.org/Mailbox">chess programming wiki</a>.
 */
public class MailboxBoard extends Board {
    private char[][] pieces;  // capital letter for white, lower-case letter for black, 0 for empty

    /**
     * Create board from FEN
     */
    public MailboxBoard(String fen) throws MalformedFENException, IllegalBoardException {
        super(fen);
    }

    /**
     * Starting position
     */
    public MailboxBoard() {
        super();
    }

    /**
     * Creates a clone of the other board
     * <p>
     * Requires: The other board is legal
     */
    public MailboxBoard(Board other) {
        super(other);
    }

    @Override
    public MailboxBoard clone() {
        return new MailboxBoard(this);
    }

    @Override
    protected char[][] getPieces() {
        // need a deep copy
        char[][] copy = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = pieces[i][j];
            }
        }
        return copy;
    }

    /**
     * Parses the piece placement array and updates the pieces variable.
     *
     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
     */
    protected void parsePiecePlacement(String[] placement) throws MalformedFENException {
        pieces = new char[8][8];
        if (placement.length != 8) {
            throw new MalformedFENException("Number of rows in piece placement field is not 8");
        }
        for (int row = 0; row < 8; row++) {
            // Since FEN goes from the top of the board to the bottom,
            // row i for pieces corresponds to index (7 - i) of the placement string
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
                        pieces[row][col++] = 0;
                    }
                } else {
                    // curChar may represent a piece
                    boolean found = false;
                    for (char valid : Util.PIECE_NAMES) {
                        if (curChar == valid) {
                            pieces[row][col++] = curChar;
                            found = true;
                            break;
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

    @Override
    protected void parseEnPassant(String enPassant) throws MalformedFENException {
        // Also checks that there is a pawn on the board for the enPassant state to be possible
        super.parseEnPassant(enPassant);
        if (enPassant.length() == 2) {
            if (enPassant.charAt(1) == '3' && pieces[3][enPassantWhite - 'a'] != 'P') {
                throw new MalformedFENException("Impossible en passant state: " + enPassant +
                        " (No white pawn was found at " + enPassantWhite + "4)");
            }
            if (enPassant.charAt(1) == '6' && pieces[4][enPassantBlack - 'a'] != 'p') {
                throw new MalformedFENException("Impossible en passant state: " + enPassant +
                        " (No black pawn was found at " + enPassantBlack + "5)");
            }
        }
    }

    @Override
    protected void checkBoardLegality() throws IllegalBoardException {
        boolean whiteKing = false;
        boolean blackKing = false;

        // Check number of kings
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (pieces[r][c] != 0) {
                    if (pieces[r][c] == 'K') {
                        if (whiteKing) {
                            throw new IllegalBoardException("More than one white kings on the board");
                        }
                        whiteKing = true;
                    } else if (pieces[r][c] == 'k') {
                        if (blackKing) {
                            throw new IllegalBoardException("More than one black kings on the board");
                        }
                        blackKing = true;
                    }
                }
            }
        }

        if (!whiteKing) {
            throw new IllegalBoardException("No white kings on the board");
        }
        if (!blackKing) {
            throw new IllegalBoardException("No black kings on the board");
        }

        // Check pawns on first or last rank
        for (int c = 0; c < 8; c++) {
            if (pieces[0][c] == 'p' || pieces[0][c] == 'P') {
                throw new IllegalBoardException("There is a pawn on rank 1");
            }
            if (pieces[7][c] == 'p' || pieces[7][c] == 'P') {
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
     * @return the set of all coordinates of a certain color (determined by the parameter white)
     * For this method and all the subsequent ones, unless otherwise specified,
     * the format of a square is consistent with the format of the pieces variable;
     * that is, {i, j} means rank (i + 1), file ('a' + j).
     */
    private Set<List<Integer>> getPieceCoords(boolean white) {
        Set<List<Integer>> coords = new HashSet<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] != 0 && (pieces[row][col] < 'a' == white)) {
                    List<Integer> lst = new ArrayList<>();
                    lst.add(row);
                    lst.add(col);
                    coords.add(lst);
                }
            }
        }
        return coords;
    }

    /**
     * @return the set of squares that white or black attacks.
     * A square is said to be "attacked" by white if putting a black king there would result in
     * the black king being in check.
     */
    private Set<List<Integer>> attacks(boolean white) {
        Set<List<Integer>> attacked = new HashSet<>();
        for (List<Integer> coord : getPieceCoords(white)) {
            attacked.addAll(attacks(coord.get(0), coord.get(1)));
        }
        return attacked;
    }

    /**
     * @return the set of squares that is attacked by the piece at {row, col}
     * <p>
     * Requires: there exists a piece at {row, col}
     */
    private Set<List<Integer>> attacks(int row, int col) {
        assert pieces[row][col] != 0;
        Set<List<Integer>> ans = new HashSet<>();
        char pieceTypeUpper = Util.toUpperCase(pieces[row][col]);

        int[][] dirs = switch (pieceTypeUpper) {
            case 'Q', 'K' -> new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            case 'R' -> new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            case 'B' -> new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            case 'N' -> new int[][]{{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
            case 'P' -> pieces[row][col] == 'P' ? new int[][]{{1, 1}, {1, -1}} : new int[][]{{-1, 1}, {-1, -1}};
            default ->
                    throw new IllegalStateException(String.format("Illegal piece at row %d, col %d: %c", row, col, pieces[row][col]));
        };

        switch (pieceTypeUpper) {
            case 'Q', 'R', 'B':
                // Infinite range
                for (int[] dir : dirs) {
                    int r = row + dir[0];
                    int c = col + dir[1];
                    while (Util.inRange(r) && Util.inRange(c)) {
                        List<Integer> sqr = new ArrayList<>();
                        sqr.add(r);
                        sqr.add(c);
                        ans.add(sqr);
                        if (pieces[r][c] != 0) {
                            // Hit an obstacle (note that this square is still attacked)
                            break;
                        }
                        r += dir[0];
                        c += dir[1];
                    }
                }
                break;
            case 'K', 'N', 'P':
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

    @Override
    public Set<Move> getLegalMoves() {
        if (winner != 'u') {
            return new HashSet<>();
        }
        Set<Move> legalMoves = new HashSet<>();
        for (List<Integer> coord : getPieceCoords(whiteToMove)) {
            legalMoves.addAll(getLegalMoves(coord.get(0), coord.get(1)));
        }
        return legalMoves;
    }

    @Override
    public Set<Move> getLegalMoves(int row, int col) {
        Set<Move> legalMoves = new HashSet<>();
        assert pieces[row][col] != 0 && (pieces[row][col] <= 'Z') == whiteToMove;
        Set<List<Integer>> candidates = attacks(row, col);
        char pieceTypeUpper = Util.toUpperCase(pieces[row][col]);

        for (List<Integer> target : candidates) {
            int endRow = target.get(0);
            int endCol = target.get(1);
            if (pieces[endRow][endCol] != 0 && (pieces[endRow][endCol] <= 'Z') == whiteToMove) {
                // Can't capture your own piece
                continue;
            }
            if (pieceTypeUpper == 'P') {
                // Pawns can only capture diagonally (which is what they attack)
                // so if there isn't a piece diagonal to the pawn, it's not a legal move
                if (pieces[endRow][endCol] == 0 || endRow == 0 || endRow == 7) {
                    // Will handle pawn capture promotions separately below
                    continue;
                }
            }
            tryRegularMove(row, col, endRow, endCol, legalMoves);
        }

        // Special rules for pawn
        // Pawns move differently from capturing
        if (pieceTypeUpper == 'P') {
            int startRow = whiteToMove ? 1 : 6;
            int promRow = whiteToMove ? 7 : 0;
            int advance = whiteToMove ? 1 : -1;
            int enPassantRow = whiteToMove ? 4 : 3;

            if (row != promRow - advance && pieces[row + advance][col] == 0) {
                tryRegularMove(row, col, row + advance, col, legalMoves);
            }
            // Pawns on starting position can move two squares
            if (row == startRow && pieces[row + advance][col] == 0 && pieces[row + 2 * advance][col] == 0) {
                tryRegularMove(row, col, row + 2 * advance, col, legalMoves);
            }
            // Promotion - Note that we don't need to specify which piece to promote to
            // because if one of the promotions is legal, then so are all others.
            if (row == promRow - advance) {
                if (pieces[promRow][col] == 0) {
                    tryPromotion(row, col, row + advance, col, legalMoves);
                }
                if (col != 0 && pieces[promRow][col - 1] != 0 && pieces[promRow][col - 1] <= 'Z' != whiteToMove) {
                    tryPromotion(row, col, row + advance, col - 1, legalMoves);
                }
                if (col != 7 && pieces[promRow][col + 1] != 0 && pieces[promRow][col + 1] <= 'Z' != whiteToMove) {
                    tryPromotion(row, col, row + advance, col + 1, legalMoves);
                }
            }
            // En passant
            if (row == enPassantRow) {
                int targetCol = -999;
                if (whiteToMove && enPassantBlack != '-') {
                    targetCol = enPassantBlack - 'a';
                } else if (!whiteToMove && enPassantWhite != '-') {
                    targetCol = enPassantWhite - 'a';
                }
                if (Util.inRange(targetCol)) {
                    // Make sure that the square contains an enemy pawn
                    assert pieces[enPassantRow][targetCol] != 0
                            && pieces[enPassantRow][targetCol] == (whiteToMove ? 'p' : 'P');
                    // Target square must be empty since the enemy pawn just moved through it
                    assert pieces[enPassantRow + advance][targetCol] == 0;
                    if (col - targetCol == 1 || col - targetCol == -1) {
                        tryEnPassant(row, col, row + advance, targetCol, legalMoves);
                    }
                }
            }
        }

        // Special rules for king
        if (pieceTypeUpper == 'K') {
            Set<List<Integer>> whiteAttacks = attacks(true);
            Set<List<Integer>> blackAttacks = attacks(false);
            if (whiteToMove && whiteCastleK) {
                // White's king and kingside rook must not have moved
                assert row == 0 && col == 4;
                assert pieces[0][7] == 'R';
                int[][] checkSquares = {{0, 4}, {0, 5}, {0, 6}};
                if (pieces[0][5] == 0 && pieces[0][6] == 0
                        && canPass(checkSquares, blackAttacks)) {
                    legalMoves.add(new Move('K'));
                }
            }
            if (whiteToMove && whiteCastleQ) {
                // White's king and queenside rook must not have moved
                assert row == 0 && col == 4;
                assert pieces[0][0] == 'R';
                int[][] checkSquares = {{0, 2}, {0, 3}, {0, 4}};
                if (pieces[0][1] == 0 && pieces[0][2] == 0 && pieces[0][3] == 0
                        && canPass(checkSquares, blackAttacks)) {
                    legalMoves.add(new Move('Q'));
                }
            }
            if (!whiteToMove && blackCastleK) {
                // Black's king and kingside rook must not have moved
                assert row == 7 && col == 4;
                assert pieces[7][7] == 'r';
                int[][] checkSquares = {{7, 4}, {7, 5}, {7, 6}};
                if (pieces[7][5] == 0 && pieces[7][6] == 0
                        && canPass(checkSquares, whiteAttacks)) {
                    legalMoves.add(new Move('k'));
                }
            }
            if (!whiteToMove && blackCastleQ) {
                // Black's king and queenside rook must not have moved
                assert row == 7 && col == 4;
                assert pieces[7][0] == 'r';
                int[][] checkSquares = {{7, 2}, {7, 3}, {7, 4}};
                if (pieces[7][1] == 0 && pieces[7][2] == 0 && pieces[7][3] == 0
                        && canPass(checkSquares, whiteAttacks)) {
                    legalMoves.add(new Move('q'));
                }
            }
        }

        return legalMoves;
    }

    /**
     * Checks whether a king can pass through all the squares without being in check.
     *
     * @param squares         the squares to check
     * @param opponentAttacks the squares attacked by the opponent
     * @return false if any square in squares is attacked by the opponent, true otherwise.
     */
    private boolean canPass(int[][] squares, Set<List<Integer>> opponentAttacks) {
        for (int[] square : squares) {
            List<Integer> lst = new ArrayList<>();
            lst.add(square[0]);
            lst.add(square[1]);
            if (opponentAttacks.contains(lst)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is a regular move (not a promotion, en passant, or castling) and it is pseudo-legal.
     */
    private void tryRegularMove(int startRow, int startCol, int endRow, int endCol, Set<Move> legalMoves) {
        tryRegOrProm(startRow, startCol, endRow, endCol, false, legalMoves);
    }

    /**
     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is a promotion and it is pseudo-legal.
     */
    private void tryPromotion(int startRow, int startCol, int endRow, int endCol, Set<Move> legalMoves) {
        tryRegOrProm(startRow, startCol, endRow, endCol, true, legalMoves);
    }

    /**
     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is an en passant and it is pseudo-legal.
     */
    private void tryEnPassant(int startRow, int startCol, int endRow, int endCol, Set<Move> legalMoves) {
        char origStart = pieces[startRow][startCol];
        char origEnemyPawn = pieces[startRow][endCol];
        // Note that the target square must be empty
        assert pieces[endRow][endCol] == 0;

        // Perform the en passant
        pieces[startRow][startCol] = 0;
        pieces[endRow][endCol] = origStart;
        pieces[startRow][endCol] = 0;
        if (!isInCheck(whiteToMove)) {
            legalMoves.add(new Move(startRow, startCol, endRow, endCol, true, true));
        }
        // Restore pieces
        pieces[startRow][startCol] = origStart;
        pieces[endRow][endCol] = 0;
        pieces[startRow][endCol] = origEnemyPawn;
    }

    /**
     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is a regular move or a promotion, not an en passant or castling.
     * Also, the move has to be "pseudo-legal" - legal if we ignore checks (that is, normally a piece of
     * that type should be able to move from {startRow, startCol} to {endRow, endCol}
     * and {endRow, endCol} can't have a piece with the same color as the current piece).
     */
    private void tryRegOrProm(int startRow, int startCol, int endRow, int endCol,
                              boolean isPromotion, Set<Move> legalMoves) {
        char origStart = pieces[startRow][startCol];
        char origEnd = pieces[endRow][endCol];
        pieces[startRow][startCol] = 0;
        pieces[endRow][endCol] = origStart;
        boolean isCapture = (origEnd != 0);
        if (!isInCheck(whiteToMove)) {
            if (isPromotion) {
                char[] promPieces = whiteToMove ? "QRBN".toCharArray() : "qrbn".toCharArray();
                for (char promPiece : promPieces) {
                    legalMoves.add(new Move(startRow, startCol, endRow, endCol, promPiece, isCapture));
                }
            } else {
                legalMoves.add(new Move(startRow, startCol, endRow, endCol, false, isCapture));
            }
        }
        // Restore pieces
        pieces[startRow][startCol] = origStart;
        pieces[endRow][endCol] = origEnd;
    }

    @Override
    public boolean isLegal(Move move) {
        return getLegalMoves().contains(move);
    }

    @Override
    public boolean isInCheck() {
        return isInCheck(whiteToMove);
    }

    /**
     * @return true if white/black is in check (determined by the parameter white), false otherwise
     */
    private boolean isInCheck(boolean white) {
        List<Integer> kingPos = getKingPos(white);
        for (List<Integer> opponent : getPieceCoords(!white)) {
            if (attacks(opponent.get(0), opponent.get(1)).contains(kingPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the position of the white/black king (determined by the parameter white)
     * Since we assume the rep invariant is true, there is only one king of each color.
     */
    private List<Integer> getKingPos(boolean white) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (pieces[r][c] == (white ? 'K' : 'k')) {
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

    @Override
    public char getWinner() {
        return winner;
    }

    @Override
    protected boolean updateWinner() {
        // Early exit to speed up performance
        boolean hasLegalMoves = false;
        for (List<Integer> coord : getPieceCoords(whiteToMove)) {
            if (!getLegalMoves(coord.get(0), coord.get(1)).isEmpty()) {
                hasLegalMoves = true;
                break;
            }
        }
        if (!hasLegalMoves) {
            if (isInCheck()) {
                // Checkmate
                winner = whiteToMove ? 'b' : 'w';
            } else {
                // Stalemate
                winner = 'd';
            }
            return true;
        }

        // Insufficient material
        if (insufficientMaterial()) {
            winner = 'd';
            return true;
        }

        // Fifty-move rule
        if (halfMove >= 100) {
            winner = 'd';
            return true;
        }

        if (!PERFT) {
            // Threefold repetition
            for (String key : posFreq.keySet()) {
                if (posFreq.get(key) >= 3) {
                    winner = 'd';
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the players have insufficient material to win the game.
     * Insufficient material means K vs. K, or KN vs. K, or KB vs. K,
     * or KB vs. KB where bishops are of the same color
     *
     * @return true if the players have insufficient material, false otherwise.
     */
    private boolean insufficientMaterial() {
        Set<List<Integer>> whitePieces = getPieceCoords(true);
        Set<List<Integer>> blackPieces = getPieceCoords(false);
        Set<List<Integer>> allPieces = new HashSet<>(whitePieces);
        allPieces.addAll(blackPieces);
        if (allPieces.size() == 2) {
            // K vs. K
            return true;
        } else if (allPieces.size() == 3) {
            // K vs. KN or K vs. KB
            for (List<Integer> piecePos : allPieces) {
                char pieceTypeUpper = Util.toUpperCase(pieces[piecePos.get(0)][piecePos.get(1)]);
                if (pieceTypeUpper == 'R' || pieceTypeUpper == 'Q' || pieceTypeUpper == 'P') {
                    return false;
                }
            }
            return true;
        } else if (whitePieces.size() == 2 && blackPieces.size() == 2) {
            // KB vs. KB
            int bishopCoordSum = -1;  // sum of row and col of one bishop
            for (List<Integer> piecePos : allPieces) {
                char pieceTypeUpper = Util.toUpperCase(pieces[piecePos.get(0)][piecePos.get(1)]);
                if (pieceTypeUpper == 'K') {
                    continue;
                }
                if (pieceTypeUpper != 'B') {
                    return false;
                }
                if (bishopCoordSum == -1) {
                    bishopCoordSum = piecePos.get(0) + piecePos.get(1);
                } else {
                    return (bishopCoordSum + piecePos.get(0) + piecePos.get(1)) % 2 == 0;
                }
            }
        }
        return false;
    }

    @Override
    public char getPieceAt(int row, int col) {
        return pieces[row][col];
    }

    @Override
    protected void setPiece(int row, int col, char pieceType) {
        pieces[row][col] = pieceType;
    }

    @Override
    protected void removePiece(int row, int col, char pieceType) {
        pieces[row][col] = 0;
    }
}
