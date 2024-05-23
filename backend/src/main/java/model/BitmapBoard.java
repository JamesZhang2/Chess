//package model;
//
//import java.util.*;
//
///**
// * Represents a legal board state using the bitmap (or bitboard) representation.
// * The pieces are represented by bitmaps, with one 64-bit integer for each (color, pieceType) pair.
// * This representation allows efficient computations through bitwise operations.
// * See <a href="https://www.chessprogramming.org/Bitboards">chess programming wiki</a>
// * for more details about Bitboards.
// */
//public class BitmapBoard implements Board {
//    // Representation Invariant: Board state is always legal at the end of a public method.
//    // This means that there is one king on both sides, no pawns are on the first or last rank,
//    // and the side to move cannot capture the opponent's king.
//    // Note that we allow the number of pieces to be arbitrary,
//    // and we don't care about whether a position is reachable from the starting position.
//
//    // Representation Invariant: The winner variable is always accurate at the end of a public method.
//
//    // Bitmaps
//    private long wPawns;
//    private long wKnights;
//    private long wBishops;
//    private long wRooks;
//    private long wQueens;
//    private long wKings;
//    private long bPawns;
//    private long bKnights;
//    private long bBishops;
//    private long bRooks;
//    private long bQueens;
//    private long bKings;
//
//    private boolean whiteToMove;
//    private boolean whiteCastleK = false, whiteCastleQ = false, blackCastleK = false, blackCastleQ = false;
//    // en passant target squares - for white it is x3, for black it is x6, where x is in [a...h]
//    // The value is '-' if there are no en passant target squares for that color.
//    private char enPassantWhite = '-', enPassantBlack = '-';
//    private int halfMove;
//    private int fullMove;
//
//    private Map<String, Integer> posFreq;  // Position frequency: How many times has a position occurred
//    // Maps the FEN string (except the halfMove and fullMove fields) to the number of times the position occurred
//
//    // w: white, b: black, d: draw, u: unknown
//    private char winner = 'u';
//
//    private PGN pgn;
//
//    private List<String> history;  // history positions stored in FEN form
//
//    // When running perft, set this to true. Otherwise, don't touch it!
//    public boolean PERFT = false;
//
//    /**
//     * Create board from FEN
//     */
//    public BitmapBoard(String fen) throws MalformedFENException, IllegalBoardException {
//        parseFen(fen);
//        checkBoardLegality();
//        this.pgn = new PGN(fullMove, whiteToMove, getResult());
//        this.history = new ArrayList<>();
//        this.posFreq = new HashMap<>();
//        posFreq.put(getUnclockedFEN(), 1);
//        updateWinner();
//    }
//
//    /**
//     * Starting position
//     */
//    public BitmapBoard() {
//        try {
//            parseFen(Util.START_POS);
//            checkBoardLegality();
//            this.pgn = new PGN(1, true, "*");
//            this.history = new ArrayList<>();
//            this.posFreq = new HashMap<>();
//            posFreq.put(getUnclockedFEN(), 1);
//            updateWinner();
//        } catch (Exception e) {
//            assert false;
//        }
//    }
//
//    /**
//     * Creates a clone of the other board
//     * <p>
//     * Requires: The other board is legal
//     */
//    public BitmapBoard(BitmapBoard other) {
//        try {
//            parseFen(other.toFEN());
//            checkBoardLegality();
//            this.winner = other.winner;
//            this.pgn = new PGN(other.pgn);
//            this.history = new ArrayList<>(other.history);
//            this.posFreq = new HashMap<>(other.posFreq);
//        } catch (Exception e) {
//            assert false;
//        }
//    }
//
//    @Override
//    public BitmapBoard clone() {
//        return new BitmapBoard(this);
//    }
//
//    @Override
//    public String toFEN() {
//        StringBuilder sb = new StringBuilder();
//
//        // Piece placement
//        for (int r = 7; r >= 0; r--) {
//            int blanks = 0;
//            for (int c = 0; c < 8; c++) {
//                if (pieces[r][c] == 0) {
//                    blanks++;
//                } else {
//                    if (blanks > 0) {
//                        sb.append(blanks);
//                    }
//                    blanks = 0;
//                    sb.append(pieces[r][c]);
//                }
//            }
//            if (blanks > 0) {
//                sb.append(blanks);
//            }
//            sb.append('/');
//        }
//        // Remove last slash
//        sb.delete(sb.length() - 1, sb.length());
//
//        // Active color
//        sb.append(' ');
//        sb.append(whiteToMove ? 'w' : 'b');
//
//        // Castling
//        sb.append(' ');
//        if (!whiteCastleK && !whiteCastleQ && !blackCastleK && !blackCastleQ) {
//            sb.append('-');
//        } else {
//            if (whiteCastleK) {
//                sb.append('K');
//            }
//            if (whiteCastleQ) {
//                sb.append('Q');
//            }
//            if (blackCastleK) {
//                sb.append('k');
//            }
//            if (blackCastleQ) {
//                sb.append('q');
//            }
//        }
//
//        // En passant
//        sb.append(' ');
//        if (enPassantWhite == '-' && enPassantBlack == '-') {
//            sb.append('-');
//        } else {
//            if (enPassantBlack == '-') {
//                sb.append(enPassantWhite);
//                sb.append(3);
//            } else {
//                sb.append(enPassantBlack);
//                sb.append(6);
//            }
//        }
//
//        // Halfmove
//        sb.append(' ');
//        sb.append(halfMove);
//
//        // Fullmove
//        sb.append(' ');
//        sb.append(fullMove);
//
//        return sb.toString();
//    }
//
//    @Override
//    public String getUnclockedFEN() {
//        // Find index of second-to-last space
//        String fen = this.toFEN();
//        int idx = fen.lastIndexOf(' ');
//        idx = fen.substring(0, idx).lastIndexOf(' ');
//        return fen.substring(0, idx);
//    }
//
//    @Override
//    public String toPGN() {
//        return pgn.toString();
//    }
//
//    /**
//     * @return the result of the game (1-0 or 1/2-1/2 or 0-1 or *)
//     */
//    private String getResult() {
//        return switch (winner) {
//            case 'u' -> "*";
//            case 'w' -> "1-0";
//            case 'd' -> "1/2-1/2";
//            case 'b' -> "0-1";
//            default -> throw new IllegalStateException("Unexpected value for winner: " + winner);
//        };
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        for (int r = 7; r >= 0; r--) {
//            for (int c = 0; c < 8; c++) {
//                sb.append(pieces[r][c] == 0 ? '.' : pieces[r][c]);
//            }
//            sb.append("\n");
//        }
//
//        if (whiteToMove) {
//            sb.append("White to move\n");
//        } else {
//            sb.append("Black to move\n");
//        }
//
//        sb.append("White O-O: ").append(whiteCastleK).append("\n");
//        sb.append("White O-O-O: ").append(whiteCastleQ).append("\n");
//        sb.append("Black O-O: ").append(blackCastleK).append("\n");
//        sb.append("Black O-O-O: ").append(blackCastleQ).append("\n");
//
//        sb.append("White en passant target square: ").append(enPassantWhite == '-' ? '-' : enPassantWhite + "3");
//        sb.append("\n");
//        sb.append("Black en passant target square: ").append(enPassantBlack == '-' ? '-' : enPassantBlack + "6");
//        sb.append("\n");
//
//        sb.append("Halfmove clock: ").append(halfMove).append("\n");
//        sb.append("Fullmove number: ").append(fullMove).append("\n");
//
//        return sb.toString();
//    }
//
//    /**
//     * Parses the FEN string and updates the board states accordingly.
//     * Note that the resulting board state may be illegal.
//     *
//     * @throws MalformedFENException if the FEN is malformed.
//     */
//    private void parseFen(String fen) throws MalformedFENException {
//        fen = fen.strip();
//        String[] fields = fen.split(" ");
//        if (fields.length != 6) {
//            throw new MalformedFENException("Number of fields in FEN is not 6");
//        }
//
//        // Piece placement
//        String[] placement = fields[0].split("/");
//        parsePiecePlacement(placement);
//
//        // Active color
//        if (fields[1].length() != 1) {
//            throw new MalformedFENException("Active color field does not have length 1");
//        }
//        if (fields[1].charAt(0) == 'w') {
//            whiteToMove = true;
//        } else if (fields[1].charAt(0) == 'b') {
//            whiteToMove = false;
//        } else {
//            throw new MalformedFENException("Unknown character in active color field: " + fields[1].charAt(0));
//        }
//
//        // Castling
//        parseCastling(fields[2]);
//
//        // En passant
//        parseEnPassant(fields[3]);
//
//        // Halfmove
//        try {
//            halfMove = Integer.parseInt(fields[4]);
//            if (halfMove < 0) {
//                throw new MalformedFENException("Halfmove field is negative: " + halfMove);
//            }
//        } catch (NumberFormatException e) {
//            throw new MalformedFENException("Halfmove field is not an integer: " + fields[4]);
//        }
//
//        // Fullmove
//        try {
//            fullMove = Integer.parseInt(fields[5]);
//            if (fullMove <= 0) {
//                throw new MalformedFENException("Fullmove field is not positive: " + fullMove);
//            }
//        } catch (NumberFormatException e) {
//            throw new MalformedFENException("Fullmove field is not an integer: " + fields[5]);
//        }
//    }
//
//    /**
//     * Parses the piece placement array and updates the pieces variable.
//     *
//     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
//     */
//    private void parsePiecePlacement(String[] placement) throws MalformedFENException {
//        if (placement.length != 8) {
//            throw new MalformedFENException("Number of rows in piece placement field is not 8");
//        }
//        for (int row = 0; row < 8; row++) {
//            // row is the index of the row in the FEN string
//            // row 0 corresponds to rank 8, which is index 7 for pieces
//            String rowStr = placement[7 - row];
//            int col = 0;
//            for (int i = 0; i < rowStr.length(); i++) {
//                if (col >= 8) {
//                    throw new MalformedFENException("Number of pieces and blanks in rank "
//                            + (8 - row) + " is greater than 8");
//                }
//                char curChar = rowStr.charAt(i);
//                if (curChar >= '1' && curChar <= '8') {
//                    // curChar represents a series of blanks
//                    int blanks = curChar - '0';
//                    if (col + blanks > 8) {
//                        throw new MalformedFENException("Number of pieces and blanks in rank "
//                                + (8 - row) + " is greater than 8");
//                    }
//                    for (int j = 0; j < blanks; j++) {
//                        pieces[row][col++] = 0;
//                    }
//                } else {
//                    // curChar may represent a piece
//                    boolean found = false;
//                    for (char valid : Util.PIECE_NAMES) {
//                        if (curChar == valid) {
//                            pieces[row][col++] = curChar;
//                            found = true;
//                        }
//                    }
//                    if (!found) {
//                        throw new MalformedFENException("Unknown piece name: " + curChar);
//                    }
//                }
//            }
//            if (col != 8) {
//                throw new MalformedFENException("Number of pieces and blanks in rank "
//                        + (8 - row) + " is less than 8");
//            }
//        }
//    }
//
//    /**
//     * Parses the castling field and updates the castling right variables.
//     *
//     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
//     */
//    private void parseCastling(String castling) throws MalformedFENException {
//        whiteCastleK = false;
//        whiteCastleQ = false;
//        blackCastleK = false;
//        blackCastleQ = false;
//        if (!(castling.length() == 1 && castling.charAt(0) == '-')) {
//            Set<Character> seen = new HashSet<>();
//            for (int i = 0; i < castling.length(); i++) {
//                char c = castling.charAt(i);
//                if (seen.contains(c)) {
//                    throw new MalformedFENException("Duplicate character in castling field: " + c);
//                }
//                seen.add(c);
//                switch (castling.charAt(i)) {
//                    case 'K':
//                        whiteCastleK = true;
//                        break;
//                    case 'Q':
//                        whiteCastleQ = true;
//                        break;
//                    case 'k':
//                        blackCastleK = true;
//                        break;
//                    case 'q':
//                        blackCastleQ = true;
//                        break;
//                    default:
//                        throw new MalformedFENException("Unknown character in castling field: " + castling.charAt(i));
//                }
//            }
//        }
//    }
//
//    /**
//     * Parses the en passant field and updates the en passant variables.
//     * <p>
//     * Requires: The pieces field and whiteToMove field have already been set based on the FEN.
//     *
//     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
//     */
//    private void parseEnPassant(String enPassant) throws MalformedFENException {
//        enPassantWhite = '-';
//        enPassantBlack = '-';
//        if (enPassant.length() == 1) {
//            if (enPassant.charAt(0) != '-') {
//                throw new MalformedFENException("Malformed en passant field: " + enPassant);
//            }
//        } else if (enPassant.length() == 2) {
//            if (enPassant.charAt(0) < 'a' || enPassant.charAt(0) > 'h') {
//                throw new MalformedFENException("Unknown file in en passant field: " + enPassant.charAt(0));
//            }
//
//            if (enPassant.charAt(1) == '3') {
//                enPassantWhite = enPassant.charAt(0);
//                if (whiteToMove) {
//                    throw new MalformedFENException("Impossible en passant state: White didn't make the last move, but en passant field is " + enPassant);
//                }
//                if (pieces[3][enPassantWhite - 'a'] != 'P') {
//                    throw new MalformedFENException("Impossible en passant state: " + enPassant +
//                            " (No white pawn was found at " + enPassantWhite + "4)");
//                }
//            } else if (enPassant.charAt(1) == '6') {
//                enPassantBlack = enPassant.charAt(0);
//                if (!whiteToMove) {
//                    throw new MalformedFENException("Impossible en passant state: Black didn't make the last move, but en passant field is " + enPassant);
//                }
//                if (pieces[4][enPassantBlack - 'a'] != 'p') {
//                    throw new MalformedFENException("Impossible en passant state: " + enPassant +
//                            " (No black pawn was found at " + enPassantBlack + "5)");
//                }
//            } else {
//                throw new MalformedFENException("Rank in en passant field must be 3 or 6");
//            }
//        } else {
//            throw new MalformedFENException("Malformed en passant field: " + enPassant);
//        }
//    }
//
//    /**
//     * Checks whether the board state is legal.
//     *
//     * @throws IllegalBoardException if the board state is illegal.
//     */
//    private void checkBoardLegality() throws IllegalBoardException {
//        boolean whiteKing = false;
//        boolean blackKing = false;
//
//        // Check number of kings
//        for (int r = 0; r < 8; r++) {
//            for (int c = 0; c < 8; c++) {
//                if (pieces[r][c] != 0) {
//                    if (pieces[r][c] == 'K') {
//                        if (whiteKing) {
//                            throw new IllegalBoardException("More than one white kings on the board");
//                        }
//                        whiteKing = true;
//                    } else if (pieces[r][c] == 'k') {
//                        if (blackKing) {
//                            throw new IllegalBoardException("More than one black kings on the board");
//                        }
//                        blackKing = true;
//                    }
//                }
//            }
//        }
//
//        if (!whiteKing) {
//            throw new IllegalBoardException("No white kings on the board");
//        }
//        if (!blackKing) {
//            throw new IllegalBoardException("No black kings on the board");
//        }
//
//        // Check pawns on first or last rank
//        for (int c = 0; c < 8; c++) {
//            if (pieces[0][c] == 'p' || pieces[0][c] == 'P') {
//                throw new IllegalBoardException("There is a pawn on rank 1");
//            }
//            if (pieces[7][c] == 'p' || pieces[7][c] == 'P') {
//                throw new IllegalBoardException("There is a pawn on rank 8");
//            }
//        }
//
//        // Check whether the player not playing is in check
//        if (isInCheck(!whiteToMove)) {
//            String opponent = whiteToMove ? "Black" : "White";
//            throw new IllegalBoardException(opponent + " is in check but it's not their move");
//        }
//    }
//
//    @Override
//    public boolean move(Move move) {
//        // Can simply check if move is in the set of all legal moves,
//        // but checking a specific piece would be more efficient.
//        int startRow = move.getStartRow();
//        int startCol = move.getStartCol();
//        int endRow = move.getEndRow();
//        int endCol = move.getEndCol();
//        char curPiece = pieces[startRow][startCol];
//        char enemyPiece = pieces[endRow][endCol];  // May be 0
//        if (curPiece == 0 || (curPiece <= 'Z' != whiteToMove)) {
//            // Can only move pieces of your color
//            return false;
//        }
//        if (enemyPiece != 0 && ((int) enemyPiece - 'a') * ((int) curPiece - 'a') > 0) {
//            // Can only take enemy pieces
//            return false;
//        }
//        if (!PERFT) {
//            // All moves tried in perft must be legal, since we iterate through all the legal moves
//            Set<Move> pieceLegalMoves = getLegalMoves(startRow, startCol);
//            if (!pieceLegalMoves.contains(move)) {
//                return false;
//            }
//        }
//        if (getWinner() != 'u') {
//            // Game already ended
//            return false;
//        }
//
//        // Move must be legal, make the move by changing board state
//        // Take a snapshot of the current state (in FEN form) and put it in history
//        history.add(this.toFEN());
//
//        // For now, we're using a verbose version of the Standard Algebraic Notation for the PGN
//        // For every non-pawn move, we include the entire starting square regardless of ambiguity
//        // TODO: Simplify SAN
//
//        StringBuilder pgnMove = new StringBuilder();
//        // Reset en passant state, will be changed below if pawn just moved two squares
//        enPassantWhite = enPassantBlack = '-';
//
//        // Remove castling rights if necessary
//        if (curPiece == 'K') {
//            whiteCastleK = false;
//            whiteCastleQ = false;
//        } else if (curPiece == 'k') {
//            blackCastleK = false;
//            blackCastleQ = false;
//        } else if (curPiece == 'R') {
//            if (startRow == 0 && startCol == 0) {
//                whiteCastleQ = false;
//            } else if (startRow == 0 && startCol == 7) {
//                whiteCastleK = false;
//            }
//        } else if (curPiece == 'r') {
//            if (startRow == 7 && startCol == 0) {
//                blackCastleQ = false;
//            } else if (startRow == 7 && startCol == 7) {
//                blackCastleK = false;
//            }
//        }
//        if (enemyPiece == 'R') {
//            if (endRow == 0 && endCol == 0) {
//                whiteCastleQ = false;
//            } else if (endRow == 0 && endCol == 7) {
//                whiteCastleK = false;
//            }
//        } else if (enemyPiece == 'r') {
//            if (endRow == 7 && endCol == 0) {
//                blackCastleQ = false;
//            } else if (endRow == 7 && endCol == 7) {
//                blackCastleK = false;
//            }
//        }
//
//        switch (move.moveType) {
//            case REGULAR:
//                if (curPiece != 'P' && curPiece != 'p') {
//                    pgnMove.append(curPiece > 'a' ? (char)(curPiece - 'a' + 'A') : curPiece);
//                    pgnMove.append(toSquare(startRow, startCol));
//                    if (move.getIsCapture()) {
//                        halfMove = 0;
//                        pgnMove.append("x");
//                    } else {
//                        halfMove++;
//                    }
//                } else {
//                    // Is a pawn move
//                    halfMove = 0;
//                    if (move.getIsCapture()) {
//                        pgnMove.append((char) (startCol + 'a'));
//                        pgnMove.append("x");
//                    }
//                    if (endRow - startRow == 2) {
//                        enPassantWhite = (char) (startCol + 'a');
//                    }
//                    if (endRow - startRow == -2) {
//                        enPassantBlack = (char) (startCol + 'a');
//                    }
//                }
//                pgnMove.append(toSquare(endRow, endCol));
//
//                // Update pieces
//                pieces[endRow][endCol] = curPiece;
//                pieces[startRow][startCol] = 0;
//                break;
//
//            case CASTLING:
//                pgnMove.append(move.getCastleType() == 'K' || move.getCastleType() == 'k' ? "O-O" : "O-O-O");
//                halfMove++;
//
//                // Update pieces
//                pieces[endRow][endCol] = curPiece;
//                pieces[startRow][startCol] = 0;
//                // Move the rook
//                int rookRow = (move.getCastleType() == 'K' || move.getCastleType() == 'Q') ? 0 : 7;
//                int rookStartCol = (move.getCastleType() == 'K' || move.getCastleType() == 'k') ? 7 : 0;
//                int rookEndCol = (move.getCastleType() == 'K' || move.getCastleType() == 'k') ? 5 : 3;
//                pieces[rookRow][rookEndCol] = pieces[rookRow][rookStartCol];
//                pieces[rookRow][rookStartCol] = 0;
//                break;
//
//            case EN_PASSANT:
//                pgnMove.append((char) (startCol + 'a'));
//                pgnMove.append("x");
//                pgnMove.append(toSquare(endRow, endCol));
//                halfMove = 0;
//
//                // Update pieces
//                pieces[endRow][endCol] = curPiece;
//                pieces[startRow][startCol] = 0;
//                // Remove the enemy pawn
//                pieces[startRow][endCol] = 0;
//                break;
//
//            case PROMOTION:
//                if (move.getIsCapture()) {
//                    pgnMove.append((char) (startCol + 'a'));
//                    pgnMove.append("x");
//                }
//                pgnMove.append(toSquare(endRow, endCol));
//                pgnMove.append("=");
//                pgnMove.append(Character.toUpperCase(move.getPromotionType()));
//                halfMove = 0;
//
//                // Update pieces
//                pieces[startRow][startCol] = 0;
//                // ending square becomes promoted piece
//                pieces[endRow][endCol] = move.getPromotionType();
//                break;
//
//            default:
//                assert false;
//        }
//
//        if (!whiteToMove) {
//            fullMove++;
//        }
//        whiteToMove = !whiteToMove;
//
//        boolean changed = updateWinner();
//
//        if (isInCheck()) {
//            if (changed) {
//                // Must be checkmate
//                pgnMove.append("#");
//            } else {
//                pgnMove.append("+");
//            }
//        }
//        pgn.addMove(pgnMove.toString());
//
//        // Update posFreq
//        if (!PERFT) {
//            String unclockedFEN = getUnclockedFEN();
//            posFreq.put(unclockedFEN, posFreq.getOrDefault(unclockedFEN, 0) + 1);
//        }
//
//        // Sanity check
//        // TODO: Can be removed after fully tested
////        try {
////            checkBoardLegality();
////        } catch (IllegalBoardException e) {
////            e.printStackTrace();
////            assert false;
////        }
//        return true;
//    }
//
//    @Override
//    public boolean undoLastMove() {
//        if (history.isEmpty()) {
//            return false;
//        }
//        String prevFEN = history.removeLast();
//
//        // Update posFreq
//        if (!PERFT) {
//            String unclockedFEN = getUnclockedFEN();
//            assert posFreq.containsKey(unclockedFEN) && posFreq.get(unclockedFEN) > 0 :
//                    String.format("unclockedFEN: %s\n posFreq: %s\n", unclockedFEN, posFreq);
//            posFreq.put(unclockedFEN, posFreq.get(unclockedFEN) - 1);
//        }
//
//        try {
//            parseFen(prevFEN);
////            checkBoardLegality();  // Sanity check, TODO: Can be removed after fully tested
//        } catch (MalformedFENException e) {
//            assert false;
////        } catch (IllegalBoardException e) {
////            e.printStackTrace();
////            assert false;
//        }
//        pgn.undoLastMove();
//        winner = 'u';
//        return true;
//    }
//
//    /**
//     * @return the set of all coordinates of a certain color (determined by the parameter white)
//     * For this method and all the subsequent ones, unless otherwise specified,
//     * the format of a square is consistent with the format of the pieces variable;
//     * that is, {i, j} means rank (i + 1), file ('a' + j).
//     */
//    private Set<List<Integer>> getPieceCoords(boolean white) {
//        Set<List<Integer>> coords = new HashSet<>();
//        for (int row = 0; row < 8; row++) {
//            for (int col = 0; col < 8; col++) {
//                if (pieces[row][col] != 0 && (pieces[row][col] < 'a' == white)) {
//                    List<Integer> lst = new ArrayList<>();
//                    lst.add(row);
//                    lst.add(col);
//                    coords.add(lst);
//                }
//            }
//        }
//        return coords;
//    }
//
//    /**
//     * @return the set of squares that white or black attacks.
//     * A square is said to be "attacked" by white if putting a black king there would result in
//     * the black king being in check.
//     */
//    private Set<List<Integer>> attacks(boolean white) {
//        Set<List<Integer>> attacked = new HashSet<>();
//        for (List<Integer> coord : getPieceCoords(white)) {
//            attacked.addAll(attacks(coord.get(0), coord.get(1)));
//        }
//        return attacked;
//    }
//
//    /**
//     * @return the set of squares that is attacked by the piece at {row, col}
//     * <p>
//     * Requires: there exists a piece at {row, col}
//     */
//    private Set<List<Integer>> attacks(int row, int col) {
//        assert pieces[row][col] != 0;
//        Set<List<Integer>> ans = new HashSet<>();
//        char pieceTypeUpper = Util.toUpperCase(pieces[row][col]);
//
//        int[][] dirs = switch (pieceTypeUpper) {
//            case 'Q', 'K' -> new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
//            case 'R' -> new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
//            case 'B' -> new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
//            case 'N' -> new int[][]{{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
//            case 'P' -> pieces[row][col] == 'P' ? new int[][]{{1, 1}, {1, -1}} : new int[][]{{-1, 1}, {-1, -1}};
//            default ->
//                    throw new IllegalStateException(String.format("Illegal piece at row %d, col %d: %c", row, col, pieces[row][col]));
//        };
//
//        switch (pieceTypeUpper) {
//            case 'Q', 'R', 'B':
//                // Infinite range
//                for (int[] dir : dirs) {
//                    int r = row + dir[0];
//                    int c = col + dir[1];
//                    while (Util.inRange(r) && Util.inRange(c)) {
//                        List<Integer> sqr = new ArrayList<>();
//                        sqr.add(r);
//                        sqr.add(c);
//                        ans.add(sqr);
//                        if (pieces[r][c] != 0) {
//                            // Hit an obstacle (note that this square is still attacked)
//                            break;
//                        }
//                        r += dir[0];
//                        c += dir[1];
//                    }
//                }
//                break;
//            case 'K', 'N', 'P':
//                // One-step range
//                for (int[] dir : dirs) {
//                    int r = row + dir[0];
//                    int c = col + dir[1];
//                    if (Util.inRange(r) && Util.inRange(c)) {
//                        List<Integer> sqr = new ArrayList<>();
//                        sqr.add(r);
//                        sqr.add(c);
//                        ans.add(sqr);
//                    }
//                }
//                break;
//        }
//        return ans;
//    }
//
//    @Override
//    public Set<Move> getLegalMoves() {
//        if (winner != 'u') {
//            return new HashSet<>();
//        }
//        Set<Move> legalMoves = new HashSet<>();
//        for (List<Integer> coord : getPieceCoords(whiteToMove)) {
//            legalMoves.addAll(getLegalMoves(coord.get(0), coord.get(1)));
//        }
//        return legalMoves;
//    }
//
//    @Override
//    public Set<Move> getLegalMoves(int row, int col) {
//        Set<Move> legalMoves = new HashSet<>();
//        assert pieces[row][col] != 0 && (pieces[row][col] <= 'Z') == whiteToMove;
//        Set<List<Integer>> candidates = attacks(row, col);
//        char pieceTypeUpper = Util.toUpperCase(pieces[row][col]);
//
//        for (List<Integer> target : candidates) {
//            int endRow = target.get(0);
//            int endCol = target.get(1);
//            if (pieces[endRow][endCol] != 0 && (pieces[endRow][endCol] <= 'Z') == whiteToMove) {
//                // Can't capture your own piece
//                continue;
//            }
//            if (pieceTypeUpper == 'P') {
//                // Pawns can only capture diagonally (which is what they attack)
//                if (pieces[endRow][endCol] == 0 || endRow == 0 || endRow == 7) {
//                    // Will handle pawn capture promotions separately below
//                    continue;
//                }
//            }
//            tryRegularMove(row, col, endRow, endCol, legalMoves);
//        }
//
//        // Special rules for pawn
//        // Pawns move differently from capturing
//        if (pieceTypeUpper == 'P') {
//            int startRow = whiteToMove ? 1 : 6;
//            int promRow = whiteToMove ? 7 : 0;
//            int advance = whiteToMove ? 1 : -1;
//            int enPassantRow = whiteToMove ? 4 : 3;
//
//            if (row != promRow - advance && pieces[row + advance][col] == 0) {
//                tryRegularMove(row, col, row + advance, col, legalMoves);
//            }
//            // Pawns on starting position can move two squares
//            if (row == startRow && pieces[row + advance][col] == 0 && pieces[row + 2 * advance][col] == 0) {
//                tryRegularMove(row, col, row + 2 * advance, col, legalMoves);
//            }
//            // Promotion - Note that we don't need to specify which piece to promote to
//            // because if one of the promotions is legal, then so are all others.
//            if (row == promRow - advance) {
//                if (pieces[promRow][col] == 0) {
//                    tryPromotion(row, col, row + advance, col, legalMoves);
//                }
//                if (col != 0 && pieces[promRow][col - 1] != 0 && pieces[promRow][col - 1] <= 'Z' != whiteToMove) {
//                    tryPromotion(row, col, row + advance, col - 1, legalMoves);
//                }
//                if (col != 7 && pieces[promRow][col + 1] != 0 && pieces[promRow][col + 1] <= 'Z' != whiteToMove) {
//                    tryPromotion(row, col, row + advance, col + 1, legalMoves);
//                }
//            }
//            // En passant
//            if (row == enPassantRow) {
//                int targetCol = -999;
//                if (whiteToMove && enPassantBlack != '-') {
//                    targetCol = enPassantBlack - 'a';
//                } else if (!whiteToMove && enPassantWhite != '-') {
//                    targetCol = enPassantWhite - 'a';
//                }
//                if (Util.inRange(targetCol)) {
//                    // Make sure that the square contains an enemy pawn
//                    assert pieces[enPassantRow][targetCol] != 0
//                            && pieces[enPassantRow][targetCol] == (whiteToMove ? 'p' : 'P');
//                    // Target square must be empty since the enemy pawn just moved through it
//                    assert pieces[enPassantRow + advance][targetCol] == 0;
//                    if (col - targetCol == 1 || col - targetCol == -1) {
//                        tryEnPassant(row, col, row + advance, targetCol, legalMoves);
//                    }
//                }
//            }
//        }
//
//        // Special rules for king
//        if (pieceTypeUpper == 'K') {
//            Set<List<Integer>> whiteAttacks = attacks(true);
//            Set<List<Integer>> blackAttacks = attacks(false);
//            if (whiteToMove && whiteCastleK) {
//                // White's king and kingside rook must not have moved
//                assert row == 0 && col == 4;
//                assert pieces[0][7] == 'R';
//                int[][] checkSquares = {{0, 4}, {0, 5}, {0, 6}};
//                if (pieces[0][5] == 0 && pieces[0][6] == 0
//                        && canPass(checkSquares, blackAttacks)) {
//                    legalMoves.add(new Move('K'));
//                }
//            }
//            if (whiteToMove && whiteCastleQ) {
//                // White's king and queenside rook must not have moved
//                assert row == 0 && col == 4;
//                assert pieces[0][0] == 'R';
//                int[][] checkSquares = {{0, 2}, {0, 3}, {0, 4}};
//                if (pieces[0][1] == 0 && pieces[0][2] == 0 && pieces[0][3] == 0
//                        && canPass(checkSquares, blackAttacks)) {
//                    legalMoves.add(new Move('Q'));
//                }
//            }
//            if (!whiteToMove && blackCastleK) {
//                // Black's king and kingside rook must not have moved
//                assert row == 7 && col == 4;
//                assert pieces[7][7] == 'r';
//                int[][] checkSquares = {{7, 4}, {7, 5}, {7, 6}};
//                if (pieces[7][5] == 0 && pieces[7][6] == 0
//                        && canPass(checkSquares, whiteAttacks)) {
//                    legalMoves.add(new Move('k'));
//                }
//            }
//            if (!whiteToMove && blackCastleQ) {
//                // Black's king and queenside rook must not have moved
//                assert row == 7 && col == 4;
//                assert pieces[7][0] == 'r';
//                int[][] checkSquares = {{7, 2}, {7, 3}, {7, 4}};
//                if (pieces[7][1] == 0 && pieces[7][2] == 0 && pieces[7][3] == 0
//                        && canPass(checkSquares, whiteAttacks)) {
//                    legalMoves.add(new Move('q'));
//                }
//            }
//        }
//
//        return legalMoves;
//    }
//
//    /**
//     * Checks whether a king can pass through all the squares without being in check.
//     *
//     * @param squares          the squares to check
//     * @param opponentAttacks the squares attacked by the opponent
//     * @return false if any square in squares is attacked by the opponent, true otherwise.
//     */
//    private boolean canPass(int[][] squares, Set<List<Integer>> opponentAttacks) {
//        for (int[] square : squares) {
//            List<Integer> lst = new ArrayList<>();
//            lst.add(square[0]);
//            lst.add(square[1]);
//            if (opponentAttacks.contains(lst)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
//     * to see if this will put the player in check.
//     * If it doesn't, add the move to legalMoves.
//     * If it does, do nothing.
//     * <p>
//     * Requires: The move is a regular move (not a promotion, en passant, or castling) and it is pseudo-legal.
//     */
//    private void tryRegularMove(int startRow, int startCol, int endRow, int endCol, Set<Move> legalMoves) {
//        tryRegOrProm(startRow, startCol, endRow, endCol, false, legalMoves);
//    }
//
//    /**
//     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
//     * to see if this will put the player in check.
//     * If it doesn't, add the move to legalMoves.
//     * If it does, do nothing.
//     * <p>
//     * Requires: The move is a promotion and it is pseudo-legal.
//     */
//    private void tryPromotion(int startRow, int startCol, int endRow, int endCol, Set<Move> legalMoves) {
//        tryRegOrProm(startRow, startCol, endRow, endCol, true, legalMoves);
//    }
//
//    /**
//     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
//     * to see if this will put the player in check.
//     * If it doesn't, add the move to legalMoves.
//     * If it does, do nothing.
//     * <p>
//     * Requires: The move is an en passant and it is pseudo-legal.
//     */
//    private void tryEnPassant(int startRow, int startCol, int endRow, int endCol, Set<Move> legalMoves) {
//        char origStart = pieces[startRow][startCol];
//        char origEnemyPawn = pieces[startRow][endCol];
//        // Note that the target square must be empty
//        assert pieces[endRow][endCol] == 0;
//
//        // Perform the en passant
//        pieces[startRow][startCol] = 0;
//        pieces[endRow][endCol] = origStart;
//        pieces[startRow][endCol] = 0;
//        if (!isInCheck(whiteToMove)) {
//            legalMoves.add(new Move(startRow, startCol, endRow, endCol, true, true));
//        }
//        // Restore pieces
//        pieces[startRow][startCol] = origStart;
//        pieces[endRow][endCol] = 0;
//        pieces[startRow][endCol] = origEnemyPawn;
//    }
//
//    /**
//     * Try to move the piece from {startRow, startCol} to {endRow, endCol}
//     * to see if this will put the player in check.
//     * If it doesn't, add the move to legalMoves.
//     * If it does, do nothing.
//     * <p>
//     * Requires: The move is a regular move or a promotion, not an en passant or castling.
//     * Also, the move has to be "pseudo-legal" - legal if we ignore checks (that is, normally a piece of
//     * that type should be able to move from {startRow, startCol} to {endRow, endCol}
//     * and {endRow, endCol} can't have a piece with the same color as the current piece).
//     */
//    private void tryRegOrProm(int startRow, int startCol, int endRow, int endCol,
//                              boolean isPromotion, Set<Move> legalMoves) {
//        char origStart = pieces[startRow][startCol];
//        char origEnd = pieces[endRow][endCol];
//        pieces[startRow][startCol] = 0;
//        pieces[endRow][endCol] = origStart;
//        boolean isCapture = (origEnd != 0);
//        if (!isInCheck(whiteToMove)) {
//            if (isPromotion) {
//                char[] promPieces = whiteToMove ? "QRBN".toCharArray() : "qrbn".toCharArray();
//                for (char promPiece : promPieces) {
//                    legalMoves.add(new Move(startRow, startCol, endRow, endCol, promPiece, isCapture));
//                }
//            } else {
//                legalMoves.add(new Move(startRow, startCol, endRow, endCol, false, isCapture));
//            }
//        }
//        // Restore pieces
//        pieces[startRow][startCol] = origStart;
//        pieces[endRow][endCol] = origEnd;
//    }
//
//    @Override
//    public boolean isLegal(Move move) {
//        return getLegalMoves().contains(move);
//    }
//
//    @Override
//    public boolean isInCheck() {
//        return isInCheck(whiteToMove);
//    }
//
//    /**
//     * @return true if white/black is in check (determined by the parameter white), false otherwise
//     */
//    private boolean isInCheck(boolean white) {
//        List<Integer> kingPos = getKingPos(white);
//        for (List<Integer> opponent : getPieceCoords(!white)) {
//            if (attacks(opponent.get(0), opponent.get(1)).contains(kingPos)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * @return the position of the white/black king (determined by the parameter white)
//     * Since we assume the rep invariant is true, there is only one king of each color.
//     */
//    private List<Integer> getKingPos(boolean white) {
//        for (int r = 0; r < 8; r++) {
//            for (int c = 0; c < 8; c++) {
//                if (pieces[r][c] == (white ? 'K' : 'k')) {
//                    List<Integer> ans = new ArrayList<>();
//                    ans.add(r);
//                    ans.add(c);
//                    return ans;
//                }
//            }
//        }
//        assert false;
//        return new ArrayList<>();
//    }
//
//    @Override
//    public char getWinner() {
//        return winner;
//    }
//
//    /**
//     * Check if the game ended and update the winner variable.
//     *
//     * @return true if the winner has changed, false otherwise.
//     */
//    private boolean updateWinner() {
//        // Early exit to speed up performance
//        boolean hasLegalMoves = false;
//        for (List<Integer> coord : getPieceCoords(whiteToMove)) {
//            if (!getLegalMoves(coord.get(0), coord.get(1)).isEmpty()) {
//                hasLegalMoves = true;
//                break;
//            }
//        }
//        if (!hasLegalMoves) {
//            if (isInCheck()) {
//                // Checkmate
//                winner = whiteToMove ? 'b' : 'w';
//            } else {
//                // Stalemate
//                winner = 'd';
//            }
//            return true;
//        }
//
//        // Insufficient material
//        if (insufficientMaterial()) {
//            winner = 'd';
//            return true;
//        }
//
//        // Fifty-move rule
//        if (halfMove >= 100) {
//            winner = 'd';
//            return true;
//        }
//
//        if (!PERFT) {
//            // Threefold repetition
//            for (String key : posFreq.keySet()) {
//                if (posFreq.get(key) >= 3) {
//                    winner = 'd';
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Check if the players have insufficient material to win the game.
//     * Insufficient material means K vs. K, or KN vs. K, or KB vs. K,
//     * or KB vs. KB where bishops are of the same color
//     *
//     * @return true if the players have insufficient material, false otherwise.
//     */
//    private boolean insufficientMaterial() {
//        Set<List<Integer>> whitePieces = getPieceCoords(true);
//        Set<List<Integer>> blackPieces = getPieceCoords(false);
//        Set<List<Integer>> allPieces = new HashSet<>(whitePieces);
//        allPieces.addAll(blackPieces);
//        if (allPieces.size() == 2) {
//            // K vs. K
//            return true;
//        } else if (allPieces.size() == 3) {
//            // K vs. KN or K vs. KB
//            for (List<Integer> piecePos : allPieces) {
//                char pieceTypeUpper = Util.toUpperCase(pieces[piecePos.get(0)][piecePos.get(1)]);
//                if (pieceTypeUpper == 'R' || pieceTypeUpper == 'Q' || pieceTypeUpper == 'P') {
//                    return false;
//                }
//            }
//            return true;
//        } else if (whitePieces.size() == 2 && blackPieces.size() == 2) {
//            // KB vs. KB
//            int bishopCoordSum = -1;  // sum of row and col of one bishop
//            for (List<Integer> piecePos : allPieces) {
//                char pieceTypeUpper = Util.toUpperCase(pieces[piecePos.get(0)][piecePos.get(1)]);
//                if (pieceTypeUpper == 'K') {
//                    continue;
//                }
//                if (pieceTypeUpper != 'B') {
//                    return false;
//                }
//                if (bishopCoordSum == -1) {
//                    bishopCoordSum = piecePos.get(0) + piecePos.get(1);
//                } else {
//                    return (bishopCoordSum + piecePos.get(0) + piecePos.get(1)) % 2 == 0;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * @return the chessboard notation for the square at {row, col}. (for example: a1, e4)
//     */
//    private String toSquare(int row, int col) {
//        return "" + ('a' + col) + (row + 1);
//    }
//}
