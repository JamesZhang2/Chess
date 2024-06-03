package model;

import java.util.*;

/**
 * This abstract class represents the board, and is inherited by MailboxBoard and BitmapBoard,
 * two different representations of the pieces. Common fields and methods are factored out in this class.
 * Methods that depend on the specific representation of the pieces are declared protected abstract
 * and will be overridden by the subclasses.
 */

public abstract class Board {
    // Representation Invariant: Board state is always legal at the end of a public method.
    // This means that there is one king on both sides, no pawns are on the first or last rank,
    // and the side to move cannot capture the opponent's king.
    // Note that we allow the number of pieces to be arbitrary,
    // and we don't care about whether a position is reachable from the starting position.

    // Representation Invariant: The winner variable is always accurate at the end of a public method.

    // pieces[i][j] is the piece at rank (i + 1), file ('a' + j)
    // For example, pieces[0][0] is the piece at a1, pieces[3][4] is the piece at d3.
    // It is 0 if there is no piece at that square

    protected boolean whiteToMove;
    protected boolean whiteCastleK = false, whiteCastleQ = false, blackCastleK = false, blackCastleQ = false;
    // en passant target squares - for white it is x3, for black it is x6, where x is in [a...h]
    // The value is '-' if there are no en passant target squares for that color.
    protected char enPassantWhite = '-', enPassantBlack = '-';
    protected int halfMove;
    protected int fullMove;

    protected Map<String, Integer> posFreq;  // Position frequency: How many times has a position occurred
    // Maps the FEN string (except the halfMove and fullMove fields) to the number of times the position occurred

    // w: white, b: black, d: draw, u: unknown
    protected char winner = 'u';

    protected PGN pgn;

    protected List<String> history;  // history positions stored in FEN form

    // When running perft, set this to true. Otherwise, don't touch it!
    public boolean PERFT = false;

    public Board(String fen) throws MalformedFENException, IllegalBoardException {
        parseFen(fen);
        checkBoardLegality();
        this.pgn = new PGN(fullMove, whiteToMove, getResult());
        this.history = new ArrayList<>();
        this.posFreq = new HashMap<>();
        posFreq.put(getUnclockedFEN(), 1);
        updateWinner();
    }

    public Board() {
        try {
            parseFen(Util.START_POS);
            checkBoardLegality();
            this.pgn = new PGN(1, true, "*");
            this.history = new ArrayList<>();
            this.posFreq = new HashMap<>();
            posFreq.put(getUnclockedFEN(), 1);
            updateWinner();
        } catch (Exception e) {
            assert false;
        }
    }

    public Board(Board other) {
        try {
            parseFen(other.toFEN());
            checkBoardLegality();
            this.winner = other.winner;
            this.pgn = new PGN(other.pgn);
            this.history = new ArrayList<>(other.history);
            this.posFreq = new HashMap<>(other.posFreq);
        } catch (Exception e) {
            assert false;
        }
    }

    @Override
    public abstract Board clone();

    /**
     * @return the FEN string representing the current board state
     */
    public String toFEN() {
        StringBuilder sb = new StringBuilder();

        // Piece placement
        sb.append(piecesToFEN());

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
     * @return return the FEN string representing the current board state
     * without the halfMove and fullMove fields
     */
    protected String getUnclockedFEN() {
        // Find index of second-to-last space
        String fen = this.toFEN();
        int idx = fen.lastIndexOf(' ');
        idx = fen.substring(0, idx).lastIndexOf(' ');
        return fen.substring(0, idx);
    }

    /**
     * @return the first field of the FEN string generated using piece placement information.
     */
    protected String piecesToFEN() {
        char[][] pieces = getPieces();
        StringBuilder sb = new StringBuilder();
        for (int r = 7; r >= 0; r--) {
            int blanks = 0;
            for (int c = 0; c < 8; c++) {
                if (pieces[r][c] == 0) {
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
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * @return the PGN of this game.
     */
    public String toPGN() {
        return pgn.toString();
    }

    /**
     * @return A readable depiction of the board state, used for debugging
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(piecesToString());

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
     * @return an 8x8 array of pieces as in the mailbox representation.
     * Modifying the returned array should not change the board state
     */
    protected abstract char[][] getPieces();

    /**
     * @return A readable depiction of the pieces, used for debugging
     */
    protected String piecesToString() {
        char[][] pieces = getPieces();
        StringBuilder sb = new StringBuilder();
        for (int r = 7; r >= 0; r--) {
            for (int c = 0; c < 8; c++) {
                sb.append(pieces[r][c] == 0 ? '.' : pieces[r][c]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Parses the FEN string and updates the board states accordingly.
     * Note that the resulting board state may be illegal.
     *
     * @throws MalformedFENException if the FEN is malformed.
     */
    protected void parseFen(String fen) throws MalformedFENException {
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
        parseCastling(fields[2]);

        // En passant
        parseEnPassant(fields[3]);

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
     * Parses the castling field and updates the castling right variables.
     *
     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
     */
    private void parseCastling(String castling) throws MalformedFENException {
        whiteCastleK = false;
        whiteCastleQ = false;
        blackCastleK = false;
        blackCastleQ = false;
        if (!(castling.length() == 1 && castling.charAt(0) == '-')) {
            Set<Character> seen = new HashSet<>();
            for (int i = 0; i < castling.length(); i++) {
                char c = castling.charAt(i);
                if (seen.contains(c)) {
                    throw new MalformedFENException("Duplicate character in castling field: " + c);
                }
                seen.add(c);
                switch (castling.charAt(i)) {
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
                        throw new MalformedFENException("Unknown character in castling field: " + castling.charAt(i));
                }
            }
        }
    }

    /**
     * Parses the en passant field and updates the en passant variables.
     * <p>
     * Requires: The pieces field and whiteToMove field have already been set based on the FEN.
     *
     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
     */
    protected void parseEnPassant(String enPassant) throws MalformedFENException {
        enPassantWhite = '-';
        enPassantBlack = '-';
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
                if (whiteToMove) {
                    throw new MalformedFENException("Impossible en passant state: White didn't make the last move, but en passant field is " + enPassant);
                }
            } else if (enPassant.charAt(1) == '6') {
                enPassantBlack = enPassant.charAt(0);
                if (!whiteToMove) {
                    throw new MalformedFENException("Impossible en passant state: Black didn't make the last move, but en passant field is " + enPassant);
                }
            } else {
                throw new MalformedFENException("Rank in en passant field must be 3 or 6");
            }
        } else {
            throw new MalformedFENException("Malformed en passant field: " + enPassant);
        }
    }

    /**
     * Parses the piece placement array and updates the pieces variable.
     *
     * @throws MalformedFENException if the FEN is malformed. In this case, the board state can be illegal.
     */
    protected abstract void parsePiecePlacement(String[] placement) throws MalformedFENException;

    /**
     * Checks whether the board state is legal.
     *
     * @throws IllegalBoardException if the board state is illegal.
     */
    protected abstract void checkBoardLegality() throws IllegalBoardException;

    /**
     * @return the piece at (row, col), or 0 if there is no piece there
     */
    public abstract char getPieceAt(int row, int col);

    /**
     * Place a piece of pieceType at (row, col)
     */
    protected abstract void setPiece(int row, int col, char pieceType);

    /**
     * Remove a piece of pieceType at (row, col)
     */
    protected abstract void removePiece(int row, int col, char pieceType);

    /**
     * If the move is legal, make the move by updating the board state (including the winner) and return true.
     * Otherwise, return false and don't change the board state.
     * <p>
     * Requires: move is of type regular, castling, promotion, or en passant.
     *
     * @return whether the move is legal
     */
    public boolean move(Move move) {
        // Can simply check if move is in the set of all legal moves,
        // but checking a specific piece would be more efficient.
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();
        char curPiece = getPieceAt(startRow, startCol);
        char enemyPiece = getPieceAt(endRow, endCol);  // May be 0
        if (curPiece == 0 || (curPiece <= 'Z' != whiteToMove)) {
            // Can only move pieces of your color
            return false;
        }
        if (enemyPiece != 0 && ((int) enemyPiece - 'a') * ((int) curPiece - 'a') > 0) {
            // Can only take enemy pieces
            return false;
        }
        if (!PERFT) {
            // All moves tried in perft must be legal, since we iterate through all the legal moves
            Set<Move> pieceLegalMoves = getLegalMoves(startRow, startCol);
            if (!pieceLegalMoves.contains(move)) {
                return false;
            }
        }
        if (getWinner() != 'u') {
            // Game already ended
            return false;
        }

        // Move must be legal, make the move by changing board state
        // Take a snapshot of the current state (in FEN form) and put it in history
        history.add(this.toFEN());

        // For now, we're using a verbose version of the Standard Algebraic Notation for the PGN
        // For every non-pawn move, we include the entire starting square regardless of ambiguity
        // TODO: Simplify SAN

        StringBuilder pgnMove = new StringBuilder();
        // Reset en passant state, will be changed below if pawn just moved two squares
        enPassantWhite = enPassantBlack = '-';

        // Remove castling rights if necessary
        if (curPiece == 'K') {
            whiteCastleK = false;
            whiteCastleQ = false;
        } else if (curPiece == 'k') {
            blackCastleK = false;
            blackCastleQ = false;
        } else if (curPiece == 'R') {
            if (startRow == 0 && startCol == 0) {
                whiteCastleQ = false;
            } else if (startRow == 0 && startCol == 7) {
                whiteCastleK = false;
            }
        } else if (curPiece == 'r') {
            if (startRow == 7 && startCol == 0) {
                blackCastleQ = false;
            } else if (startRow == 7 && startCol == 7) {
                blackCastleK = false;
            }
        }
        if (enemyPiece == 'R') {
            if (endRow == 0 && endCol == 0) {
                whiteCastleQ = false;
            } else if (endRow == 0 && endCol == 7) {
                whiteCastleK = false;
            }
        } else if (enemyPiece == 'r') {
            if (endRow == 7 && endCol == 0) {
                blackCastleQ = false;
            } else if (endRow == 7 && endCol == 7) {
                blackCastleK = false;
            }
        }

        switch (move.moveType) {
            case REGULAR:
                if (curPiece != 'P' && curPiece != 'p') {
                    pgnMove.append(curPiece > 'a' ? (char) (curPiece - 'a' + 'A') : curPiece);
                    pgnMove.append(toSquare(startRow, startCol));
                    if (move.getIsCapture()) {
                        halfMove = 0;
                        pgnMove.append("x");
                    } else {
                        halfMove++;
                    }
                } else {
                    // Is a pawn move
                    halfMove = 0;
                    if (move.getIsCapture()) {
                        pgnMove.append((char) (startCol + 'a'));
                        pgnMove.append("x");
                    }
                    if (endRow - startRow == 2) {
                        enPassantWhite = (char) (startCol + 'a');
                    }
                    if (endRow - startRow == -2) {
                        enPassantBlack = (char) (startCol + 'a');
                    }
                }
                pgnMove.append(toSquare(endRow, endCol));

                // Update pieces
                if (enemyPiece != 0) {
                    removePiece(endRow, endCol, enemyPiece);
                }
                setPiece(endRow, endCol, curPiece);
                removePiece(startRow, startCol, curPiece);
                break;

            case CASTLING:
                pgnMove.append(move.getCastleType() == 'K' || move.getCastleType() == 'k' ? "O-O" : "O-O-O");
                halfMove++;

                // Update pieces
                setPiece(endRow, endCol, curPiece);
                removePiece(startRow, startCol, curPiece);
                // Move the rook
                int rookRow = (move.getCastleType() == 'K' || move.getCastleType() == 'Q') ? 0 : 7;
                int rookStartCol = (move.getCastleType() == 'K' || move.getCastleType() == 'k') ? 7 : 0;
                int rookEndCol = (move.getCastleType() == 'K' || move.getCastleType() == 'k') ? 5 : 3;
                setPiece(rookRow, rookEndCol, whiteToMove ? 'R' : 'r');
                removePiece(rookRow, rookStartCol, whiteToMove ? 'R' : 'r');
                break;

            case EN_PASSANT:
                pgnMove.append((char) (startCol + 'a'));
                pgnMove.append("x");
                pgnMove.append(toSquare(endRow, endCol));
                halfMove = 0;

                // Update pieces
                setPiece(endRow, endCol, curPiece);
                removePiece(startRow, startCol, curPiece);
                // Remove the enemy pawn
                removePiece(startRow, endCol, curPiece == 'P' ? 'p' : 'P');
                break;

            case PROMOTION:
                if (move.getIsCapture()) {
                    pgnMove.append((char) (startCol + 'a'));
                    pgnMove.append("x");
                }
                pgnMove.append(toSquare(endRow, endCol));
                pgnMove.append("=");
                pgnMove.append(Character.toUpperCase(move.getPromotionType()));
                halfMove = 0;

                // Update pieces
                if (enemyPiece != 0) {
                    removePiece(endRow, endCol, enemyPiece);
                }
                removePiece(startRow, startCol, whiteToMove ? 'P' : 'p');
                // ending square becomes promoted piece
                setPiece(endRow, endCol, move.getPromotionType());
                break;

            default:
                assert false;
        }

        if (!whiteToMove) {
            fullMove++;
        }
        whiteToMove = !whiteToMove;

        boolean changed = updateWinner();

        if (isInCheck()) {
            if (changed) {
                // Must be checkmate
                pgnMove.append("#");
            } else {
                pgnMove.append("+");
            }
        }
        pgn.addMove(pgnMove.toString());

        // Update posFreq
        if (!PERFT) {
            String unclockedFEN = getUnclockedFEN();
            posFreq.put(unclockedFEN, posFreq.getOrDefault(unclockedFEN, 0) + 1);
        }

        // Sanity check
        // TODO: Can be removed after fully tested
//        try {
//            checkBoardLegality();
//        } catch (IllegalBoardException e) {
//            e.printStackTrace();
//            assert false;
//        }
        return true;
    }

    /**
     * Undo the last move and restore the board to the same state as the one before the move.
     * If the current board state is already the initial state (when the board was loaded), do nothing.
     *
     * @return false if the board state is already the initial state, true otherwise.
     */
    public boolean undoLastMove() {
        if (history.isEmpty()) {
            return false;
        }
        String prevFEN = history.removeLast();

        // Update posFreq
        if (!PERFT) {
            String unclockedFEN = getUnclockedFEN();
            assert posFreq.containsKey(unclockedFEN) && posFreq.get(unclockedFEN) > 0 :
                    String.format("unclockedFEN: %s\n posFreq: %s\n", unclockedFEN, posFreq);
            posFreq.put(unclockedFEN, posFreq.get(unclockedFEN) - 1);
        }

        try {
            parseFen(prevFEN);
//            checkBoardLegality();  // Sanity check, TODO: Can be removed after fully tested
        } catch (MalformedFENException e) {
            assert false;
//        } catch (IllegalBoardException e) {
//            e.printStackTrace();
//            assert false;
        }
        pgn.undoLastMove();
        winner = 'u';
        return true;
    }

    /**
     * @return the set of legal moves in the current position
     */
    public abstract Set<Move> getLegalMoves();

    /**
     * @return the set of legal moves for the piece at position {row, col}.
     * <p>
     * Requires: There is a piece at {row, col} and the color of the piece is the same
     * as the current player
     */
    public abstract Set<Move> getLegalMoves(int row, int col);

    /**
     * Only checks whether move is legal or not, does not change the board state.
     *
     * @return whether the move is legal
     */
    public abstract boolean isLegal(Move move);

    /**
     * @return true if the side to move is currently in check, false otherwise
     */
    public abstract boolean isInCheck();

    /**
     * @return the winner of the game.
     */
    public abstract char getWinner();

    /**
     * Check if the game ended and update the winner variable.
     *
     * @return true if the winner has changed, false otherwise.
     */
    protected boolean updateWinner() {
        if (!hasLegalMoves()) {
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
    protected abstract boolean insufficientMaterial();

    /**
     * @return whether the current player has legal moves
     * Should use early exit to speed up performance
     */
    protected abstract boolean hasLegalMoves();

    /**
     * @return true if it's white to move, false otherwise
     */
    public boolean whiteToMove() {
        return whiteToMove;
    }

    /**
     * @return the result of the game (1-0 or 1/2-1/2 or 0-1 or *)
     */
    public String getResult() {
        return switch (winner) {
            case 'u' -> "*";
            case 'w' -> "1-0";
            case 'd' -> "1/2-1/2";
            case 'b' -> "0-1";
            default -> throw new IllegalStateException("Unexpected value for winner: " + winner);
        };
    }

    /**
     * @return the chessboard notation for the square at {row, col}. (for example: a1, e4)
     */
    private String toSquare(int row, int col) {
        return "" + ('a' + col) + (row + 1);
    }
}
