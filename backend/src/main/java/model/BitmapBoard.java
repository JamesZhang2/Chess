package model;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents a legal board state using the bitmap (or bitboard) representation.
 * The pieces are represented by bitmaps, with one 64-bit integer for each (color, pieceType) pair.
 * This representation allows efficient computations through bitwise operations.
 * See <a href="https://www.chessprogramming.org/Bitboards">chess programming wiki</a>
 * for more details about Bitboards.
 */
public class BitmapBoard extends Board {
    // Bitmaps
    // Least significant bit represents a1, second least significant bit represents b1, and so on,
    // Most significant bit represents h8
    long[] bitmaps;  // for example, bitmaps['P'] is the bitmap for the white pawn

    /**
     * Create board from FEN
     */
    public BitmapBoard(String fen) throws MalformedFENException, IllegalBoardException {
        super(fen);
    }

    /**
     * Starting position
     */
    public BitmapBoard() {
        super();
    }

    /**
     * Creates a clone of the other board
     * <p>
     * Requires: The other board is legal
     */
    public BitmapBoard(Board other) {
        super(other);
    }

    @Override
    public BitmapBoard clone() {
        return new BitmapBoard(this);
    }

    @Override
    protected char[][] getPieces() {
        char[][] pieces = new char[8][8];
        for (char pieceType : Util.PIECE_NAMES) {
            populatePieces(pieces, pieceType);
        }
        return pieces;
    }

    /**
     * Put all pieces of pieceType to the right locations in pieces
     */
    private void populatePieces(char[][] pieces, char pieceType) {
        long bitmap = bitmaps[pieceType];
        while (bitmap != 0) {
            int idx = Util.getLS1BIdx(bitmap);
            pieces[idx / 8][idx % 8] = pieceType;
            bitmap = Util.resetLS1B(bitmap);
        }
    }

    /**
     * Parses the piece placement array and updates the pieces variable.
     *
     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
     */
    protected void parsePiecePlacement(String[] placement) throws MalformedFENException {
        bitmaps = new long['z' + 1];
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
                    col += (curChar - '0');
                } else {
                    // curChar may represent a piece
                    boolean found = false;
                    for (char valid : Util.PIECE_NAMES) {
                        if (curChar == valid) {
                            bitmaps[curChar] = Util.setBit(bitmaps[curChar], row, col);
                            found = true;
                            col++;
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
            if (enPassant.charAt(1) == '3' && !Util.getBit(bitmaps['P'], 3, enPassantWhite - 'a')) {
                throw new MalformedFENException("Impossible en passant state: " + enPassant +
                        " (No white pawn was found at " + enPassantWhite + "4)");
            }
            if (enPassant.charAt(1) == '6' && !Util.getBit(bitmaps['p'], 4, enPassantBlack - 'a')) {
                throw new MalformedFENException("Impossible en passant state: " + enPassant +
                        " (No black pawn was found at " + enPassantBlack + "5)");
            }
        }
    }

    @Override
    protected void checkBoardLegality() throws IllegalBoardException {
        // Check number of kings
        if (Util.popCount(bitmaps['K']) > 1) {
            throw new IllegalBoardException("More than one white kings on the board");
        }
        if (Util.popCount(bitmaps['k']) > 1) {
            throw new IllegalBoardException("More than one black kings on the board");
        }
        if (Util.popCount(bitmaps['K']) == 0) {
            throw new IllegalBoardException("No white kings on the board");
        }
        if (Util.popCount(bitmaps['k']) == 0) {
            throw new IllegalBoardException("No black kings on the board");
        }

        // Check pawns on first or last rank
        if ((bitmaps['P'] & Util.RANK_1) != 0) {
            throw new IllegalBoardException("There is a white pawn on rank 1");
        }
        if ((bitmaps['p'] & Util.RANK_1) != 0) {
            throw new IllegalBoardException("There is a black pawn on rank 1");
        }
        if ((bitmaps['P'] & Util.RANK_8) != 0) {
            throw new IllegalBoardException("There is a white pawn on rank 8");
        }
        if ((bitmaps['p'] & Util.RANK_8) != 0) {
            throw new IllegalBoardException("There is a black pawn on rank 8");
        }

        // Check whether the player not playing is in check
        if (isInCheck(!whiteToMove)) {
            String opponent = whiteToMove ? "Black" : "White";
            throw new IllegalBoardException(opponent + " is in check but it's not their move");
        }

        // Check whether the bitmap representation is conflicting (i.e. if there are two pieces on the same square)
        long bitmap = 0;
        for (char pieceType : Util.PIECE_NAMES) {
            if ((bitmap & bitmaps[pieceType]) != 0) {
                throw new IllegalBoardException("Two pieces are on the same square");
            }
            bitmap = bitmap | bitmaps[pieceType];
        }
    }

    /**
     * @return the bitmap of squares that white or black attacks.
     * A square is said to be "attacked" by white if putting a black king there would result in
     * the black king being in check.
     */
    private long attacks(boolean white) {
        long allPieces = getAllPieces();
        long attacked = 0;
        for (char pieceType: (white ? Util.WHITE_PIECE_NAMES : Util.BLACK_PIECE_NAMES)) {
            attacked |= attacks(bitmaps[pieceType], pieceType, allPieces);
        }
        return attacked;
    }

    /**
     * @param bitmap the bitmap of a certain type of piece
     * @param pieceType the type of piece (case-sensitive to represent white or black)
     * @param allPieces the bitmap for all pieces on the board
     * @return the bitmap of squares that is attacked by the pieces with type pieceType
     */
    private long attacks(long bitmap, char pieceType, long allPieces) {
        long attacked = 0;
        while (bitmap != 0) {
            int idx = Util.getLS1BIdx(bitmap);
            attacked |= attacks(idx, pieceType, allPieces);
            bitmap = Util.resetLS1B(bitmap);
        }
        return attacked;
    }

    /**
     * @param idx the index of a piece
     * @param pieceType the type of piece (case-sensitive to represent white or black)
     * @param allPieces the bitmap for all pieces on the board
     * @return the bitmap of squares that is attacked by the piece at idx
     */
    private long attacks(int idx, char pieceType, long allPieces) {
        long attacked = 0;
        switch (pieceType) {
            case 'p':
                return Lookup.BLACK_PAWN_ATTACK[idx];
            case 'P':
                return Lookup.WHITE_PAWN_ATTACK[idx];
            case 'N', 'n':
                return Lookup.KNIGHT_ATTACK[idx];
            case 'K', 'k':
                return Lookup.KING_ATTACK[idx];
            case 'R', 'r', 'Q', 'q':
                // north
                attacked |= getAttackInDir(idx, 8, i -> i / 8 <= 7, allPieces);
                // south
                attacked |= getAttackInDir(idx, -8, i -> i >= 0, allPieces);
                // east
                // i % 8 == 0 means that we have wrapped around and reached the a file a rank higher
                attacked |= getAttackInDir(idx, 1, i -> i % 8 != 0, allPieces);
                // west
                // Note that (-1) % 8 == -1 so +8 is necessary
                attacked |= getAttackInDir(idx, -1, i -> (i + 8) % 8 != 7, allPieces);
                if (pieceType == 'R' || pieceType == 'r') {
                    return attacked;
                }
                // Queen falls through to the bishop branch
            case 'B', 'b':
                // northeast
                attacked |= getAttackInDir(idx, 9, i -> (i % 8 != 0) && (i / 8 <= 7), allPieces);
                // southeast
                attacked |= getAttackInDir(idx, -7, i -> (i % 8 != 0) && (i >= 0), allPieces);
                // southwest
                // Here (i + 8) % 8 is unnecessary since we also check that i is non-negative
                attacked |= getAttackInDir(idx, -9, i -> (i % 8 != 7) && (i >= 0), allPieces);
                // northwest
                attacked |= getAttackInDir(idx, 7, i -> (i % 8 != 7) && (i / 8 <= 7), allPieces);
                return attacked;
            default:
                assert false;
        }
        return 0;
    }

    /**
     * Get attacks in a specific direction
     * @param idx index of piece to generate attacks for
     * @param step the amount to add in each loop iteration; represents the direction
     * @param condition condition on the index to stay in the loop
     * @param allPieces the bitmap of all pieces on the board
     * @return the bitmap of attacks of the piece at idx in the direction indicated by step
     */
    private long getAttackInDir(int idx, int step, Predicate<Integer> condition, long allPieces) {
        long attacked = 0;
        // Note that the square that the piece is on is not considered attacked by the piece
        for (int i = idx + step; condition.test(i); i += step) {
            assert i >= 0 : i;
            attacked |= (1L << i);
            if (Util.getBit(allPieces, i)) {
                // found blocking piece
                return attacked;
            }
        }
        return attacked;
    }

    /**
     * @param white if true, then we have the white pieces. Otherwise, we have the black pieces.
     * @return the bitmap for friendly pieces
     */
    private long getFriendlyPieces(boolean white) {
        long friendlyPieces = 0;
        for (char pieceType : (white ? Util.WHITE_PIECE_NAMES : Util.BLACK_PIECE_NAMES)) {
            friendlyPieces |= bitmaps[pieceType];
        }
        return friendlyPieces;
    }

    /**
     * @param white if true, then we have the white pieces. Otherwise, we have the black pieces.
     * @return the bitmap for friendly pieces
     */
    private long getEnemyPieces(boolean white) {
        return getFriendlyPieces(!white);
    }

    /**
     * @return the bitmap for all pieces
     */
    private long getAllPieces() {
        long pieces = 0;
        for (char pieceType : Util.PIECE_NAMES) {
            pieces |= bitmaps[pieceType];
        }
        return pieces;
    }

    @Override
    public Set<Move> getLegalMoves() {
        if (winner != 'u') {
            return new HashSet<>();
        }
        long friendly = getFriendlyPieces(whiteToMove);
        long enemy = getEnemyPieces(whiteToMove);
        long enemyAttacks = attacks(!whiteToMove);
        Set<Move> legalMoves = new HashSet<>();
        for (char pieceType : whiteToMove ? Util.WHITE_PIECE_NAMES : Util.BLACK_PIECE_NAMES) {
            accLegalMoves(bitmaps[pieceType], pieceType, friendly, enemy, enemyAttacks, legalMoves);
        }
        return legalMoves;
    }

    /**
     * Add the set of legal moves of a certain piece type to legalMoves
     * @param bitmap the bitmap of the pieceType
     * @param pieceType the type of piece (case-sensitive to represent white or black)
     * @param friendly the bitmap of friendly pieces
     * @param enemy the bitmap of enemy pieces
     * @param enemyAttacks the bitmap of enemy attacks
     * @param legalMoves accumulator
     */
    private void accLegalMoves(long bitmap, char pieceType, long friendly, long enemy, long enemyAttacks,
                               Set<Move> legalMoves) {
        while (bitmap != 0) {
            int ls1b = Util.getLS1BIdx(bitmap);
            accLegalMoves(ls1b, pieceType, friendly, enemy, enemyAttacks, legalMoves);
            bitmap = Util.resetLS1B(bitmap);
        }
    }

    /**
     * Add the set of legal moves for the piece at idx to legalMoves.
     * @param idx the index of the piece
     * @param pieceType the type of piece (case-sensitive to represent white or black)
     * @param friendly the bitmap of friendly pieces
     * @param enemy the bitmap of enemy pieces
     * @param enemyAttacks the bitmap of enemy attacks
     * @param legalMoves accumulator
     * <p>
     * Requires: There is a piece at idx and the color of the piece is the same
     * as the current player
     */
    private void accLegalMoves(int idx, char pieceType, long friendly, long enemy, long enemyAttacks,
                               Set<Move> legalMoves) {
        assert Util.getBit(bitmaps[pieceType], idx) && (pieceType <= 'Z') == whiteToMove;
        long allPieces = friendly | enemy;
        long attacks = attacks(idx, pieceType, allPieces);  // candidate target squares
        int row = idx / 8;
        int col = idx % 8;

        while (attacks != 0) {
            int ls1b = Util.getLS1BIdx(attacks);
            attacks = Util.resetLS1B(attacks);
            if (Util.getBit(friendly, ls1b)) {
                // Can't capture your own piece
                continue;
            }
            int endRow = ls1b / 8;
            int endCol = ls1b % 8;
            if (pieceType == 'P' || pieceType == 'p') {
                // Pawns can only capture diagonally (which is what they attack)
                // so if there isn't a piece diagonal to the pawn, it's not a legal move
                if (!Util.getBit(allPieces, ls1b) || endRow == 0 || endRow == 7) {
                    // Will handle pawn capture promotions separately below
                    continue;
                }
            }
            tryRegularMove(row, col, pieceType, endRow, endCol, legalMoves);
        }

        // Special rules for pawn
        // Pawns move differently from capturing
        if (pieceType == 'P' || pieceType == 'p') {
            int startRow = whiteToMove ? 1 : 6;
            int promRow = whiteToMove ? 7 : 0;
            int advance = whiteToMove ? 1 : -1;
            int enPassantRow = whiteToMove ? 4 : 3;

            if (row != promRow - advance && !Util.getBit(allPieces, row + advance, col)) {
                tryRegularMove(row, col, pieceType, row + advance, col, legalMoves);
            }
            // Pawns on starting position can move two squares
            if (row == startRow && !Util.getBit(allPieces, row + advance, col)
                    && !Util.getBit(allPieces, row + 2 * advance, col)) {
                tryRegularMove(row, col, pieceType, row + 2 * advance, col, legalMoves);
            }
            // Promotion - Note that we don't need to specify which piece to promote to
            // because if one of the promotions is legal, then so are all others.
            if (row == promRow - advance) {
                if (!Util.getBit(allPieces, promRow, col)) {
                    tryPromotion(row, col, pieceType, row + advance, col, legalMoves);
                }
                if (col != 0 && Util.getBit(enemy, promRow, col - 1)) {
                    tryPromotion(row, col, pieceType, row + advance, col - 1, legalMoves);
                }
                if (col != 7 && Util.getBit(enemy, promRow, col + 1)) {
                    tryPromotion(row, col, pieceType, row + advance, col + 1, legalMoves);
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
                    assert Util.getBit(bitmaps[whiteToMove ? 'p' : 'P'], enPassantRow, targetCol);
                    // Target square must be empty since the enemy pawn just moved through it
                    assert !Util.getBit(allPieces, enPassantRow + advance, targetCol);
                    if (col - targetCol == 1 || col - targetCol == -1) {
                        tryEnPassant(row, col, pieceType, row + advance, targetCol, legalMoves);
                    }
                }
            }
        }

        // Special rules for king
        if (pieceType == 'K' || pieceType == 'k') {
            if (whiteToMove && whiteCastleK) {
                // White's king and kingside rook must not have moved
                assert row == 0 && col == 4;
                assert Util.getBit(bitmaps['R'], 0, 7);
                // 0x60: f1, g1; 0x70: e1, f1, g1
                if ((allPieces & 0x60L) == 0 && (enemyAttacks & 0x70L) == 0) {
                    legalMoves.add(new Move('K'));
                }
            }
            if (whiteToMove && whiteCastleQ) {
                // White's king and queenside rook must not have moved
                assert row == 0 && col == 4;
                assert Util.getBit(bitmaps['R'], 0, 0);
                // 0xE: b1, c1, d1; 0x1C: c1, d1, e1
                if ((allPieces & 0xEL) == 0 && (enemyAttacks & 0x1CL) == 0) {
                    legalMoves.add(new Move('Q'));
                }
            }
            if (!whiteToMove && blackCastleK) {
                // Black's king and kingside rook must not have moved
                assert row == 7 && col == 4;
                assert Util.getBit(bitmaps['r'], 7, 7);
                // 0x60 << 56: f8, g8; 0x70 << 56: e8, f8, g8
                if ((allPieces & (0x60L << 56)) == 0 && (enemyAttacks & (0x70L << 56)) == 0) {
                    legalMoves.add(new Move('k'));
                }
            }
            if (!whiteToMove && blackCastleQ) {
                // Black's king and queenside rook must not have moved
                assert row == 7 && col == 4;
                assert Util.getBit(bitmaps['r'], 7, 0);
                // 0xE << 56: b8, c8, d8; 0x1C << 56: c8, d8, e8
                if ((allPieces & (0xEL << 56)) == 0 && (enemyAttacks & (0x1CL << 56)) == 0) {
                    legalMoves.add(new Move('q'));
                }
            }
        }
    }

    @Override
    public Set<Move> getLegalMoves(int row, int col) {
        long friendly = getFriendlyPieces(whiteToMove);
        long enemy = getEnemyPieces(whiteToMove);
        long enemyAttacks = attacks(!whiteToMove);
        char pieceType = getPieceAt(row, col);
        assert pieceType != 0 && (pieceType <= 'Z') == whiteToMove;
        Set<Move> legalMoves = new HashSet<>();
        accLegalMoves(row * 8 + col, pieceType, friendly, enemy, enemyAttacks, legalMoves);
        return legalMoves;
    }

    /**
     * Try to move the piece of type pieceType from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is a regular move (not a promotion, en passant, or castling) and it is pseudo-legal.
     */
    private void tryRegularMove(int startRow, int startCol, char pieceType,
                                int endRow, int endCol, Set<Move> legalMoves) {
        tryRegOrProm(startRow, startCol, pieceType, endRow, endCol, false, legalMoves);
    }

    /**
     * Try to move the piece of type pieceType from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is a promotion and it is pseudo-legal.
     */
    private void tryPromotion(int startRow, int startCol, char pieceType,
                              int endRow, int endCol, Set<Move> legalMoves) {
        tryRegOrProm(startRow, startCol, pieceType, endRow, endCol, true, legalMoves);
    }

    /**
     * Try to move the piece of type pieceType from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is an en passant and it is pseudo-legal.
     */
    private void tryEnPassant(int startRow, int startCol, char pieceType,
                              int endRow, int endCol, Set<Move> legalMoves) {
        assert pieceType == 'P' || pieceType == 'p';
        char enemyType = pieceType == 'P' ? 'p' : 'P';
        // Note that the target square must be empty
        assert !Util.getBit(getAllPieces(), endRow, endCol);

        // Perform the en passant
        bitmaps[pieceType] = Util.clearBit(bitmaps[pieceType], startRow, startCol);
        bitmaps[pieceType] = Util.setBit(bitmaps[pieceType], endRow, endCol);
        bitmaps[enemyType] = Util.clearBit(bitmaps[enemyType], startRow, endCol);
        if (!isInCheck(whiteToMove)) {
            legalMoves.add(new Move(startRow, startCol, endRow, endCol, true, true));
        }
        // Restore pieces
        bitmaps[pieceType] = Util.setBit(bitmaps[pieceType], startRow, startCol);
        bitmaps[pieceType] = Util.clearBit(bitmaps[pieceType], endRow, endCol);
        bitmaps[enemyType] = Util.setBit(bitmaps[enemyType], startRow, endCol);
    }

    /**
     * Try to move the piece of type pieceType from {startRow, startCol} to {endRow, endCol}
     * to see if this will put the player in check.
     * If it doesn't, add the move to legalMoves.
     * If it does, do nothing.
     * <p>
     * Requires: The move is a regular move or a promotion, not an en passant or castling.
     * Also, the move has to be "pseudo-legal" - legal if we ignore checks (that is, normally a piece of
     * that type should be able to move from {startRow, startCol} to {endRow, endCol}
     * and {endRow, endCol} can't have a piece with the same color as the current piece).
     */
    private void tryRegOrProm(int startRow, int startCol, char pieceType,
                              int endRow, int endCol, boolean isPromotion, Set<Move> legalMoves) {
        // piece type at (endRow, endCol) or 0 if it's empty
        char enemyType = getPieceAt(endRow, endCol);
        boolean isCapture = (enemyType != 0);
        bitmaps[pieceType] = Util.clearBit(bitmaps[pieceType], startRow, startCol);
        bitmaps[pieceType] = Util.setBit(bitmaps[pieceType], endRow, endCol);
        if (enemyType != 0) {
            bitmaps[enemyType] = Util.clearBit(bitmaps[enemyType], endRow, endCol);
        }
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
        bitmaps[pieceType] = Util.setBit(bitmaps[pieceType], startRow, startCol);
        bitmaps[pieceType] = Util.clearBit(bitmaps[pieceType], endRow, endCol);
        if (enemyType != 0) {
            bitmaps[enemyType] = Util.setBit(bitmaps[enemyType], endRow, endCol);
        }
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
        return (attacks(!white) & bitmaps[white ? 'K' : 'k']) != 0;
    }

    @Override
    public char getWinner() {
        return winner;
    }

    @Override
    protected boolean hasLegalMoves() {
        HashSet<Move> acc = new HashSet<>();
        long friendly = getFriendlyPieces(whiteToMove);
        long enemy = getEnemyPieces(whiteToMove);
        long enemyAttacks = attacks(!whiteToMove);
        for (char type : whiteToMove ? Util.WHITE_PIECE_NAMES : Util.BLACK_PIECE_NAMES) {
            long bitmap = bitmaps[type];
            while (bitmap != 0) {
                int idx = Util.getLS1BIdx(bitmap);
                accLegalMoves(idx, type, friendly, enemy, enemyAttacks, acc);
                if (!acc.isEmpty()) {
                    return true;
                }
                bitmap = Util.resetLS1B(bitmap);
            }
        }
        return false;
    }

    @Override
    protected boolean insufficientMaterial() {
        int numWhitePieces = Util.popCount(getFriendlyPieces(true));
        int numBlackPieces = Util.popCount(getFriendlyPieces(false));
        int numPieces = numWhitePieces + numBlackPieces;
        if (numPieces == 2) {
            // K vs. K
            return true;
        } else if (numPieces == 3) {
            char[] unwantedTypes = {'P', 'R', 'Q', 'p', 'r', 'q'};
            // K vs. KN or K vs. KB
            for (char type : unwantedTypes) {
                if (bitmaps[type] != 0) {
                    return false;
                }
            }
            return true;
        } else if (numWhitePieces == 2 && numBlackPieces == 2) {
            // KB vs. KB
            if (bitmaps['B'] == 0 || bitmaps['b'] == 0) {
                return false;
            }
            int whiteBIdx = Util.getLS1BIdx(bitmaps['B']);
            int blackBIdx = Util.getLS1BIdx(bitmaps['b']);
            return (whiteBIdx / 8 + whiteBIdx % 8) % 2 == (blackBIdx / 8 + blackBIdx % 8) % 2;
        }
        return false;
    }

    @Override
    public char getPieceAt(int row, int col) {
        for (char pieceType : Util.PIECE_NAMES) {
            if (Util.getBit(bitmaps[pieceType], row, col)) {
                return pieceType;
            }
        }
        return 0;
    }

    @Override
    protected void setPiece(int row, int col, char pieceType) {
        bitmaps[pieceType] = Util.setBit(bitmaps[pieceType], row, col);
    }

    @Override
    protected void removePiece(int row, int col, char pieceType) {
        bitmaps[pieceType] = Util.clearBit(bitmaps[pieceType], row, col);
    }
}
