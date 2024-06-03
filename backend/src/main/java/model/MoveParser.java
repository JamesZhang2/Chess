package model;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for parsing Standard Algebraic Notation (SAN) into Move objects
 */
public class MoveParser {
    /**
     * The input string should represent a move in Standard Algebraic Notation (SAN): see
     * <a href="https://en.wikipedia.org/wiki/Algebraic_notation_(chess)">this wikipedia article</a> for more details.
     * <p>
     * Captures are indicated by 'x', promotions indicated by '=' followed by piece type,
     * Castling is either O-O, O-O-O, 0-0, or 0-0-0
     * Checks (+) and checkmates (#) do not have to be present, but they must be correct if present,
     * and they must be at the end
     * En passant target square is the square that the pawn moved to, not the square of the enemy pawn
     * The e.p. notation is not supported
     * <p>
     *
     * @return the move parsed from the input string, given the current board state
     * @throws MalformedMoveException if the input represents a malformed move
     * @throws IllegalMoveException   if the input represents an illegal move
     * @throws AmbiguousMoveException if the input represents an ambiguous move
     *                                <p>
     *                                Postcondition: The board is unchanged
     */
    public static Move parse(String input, Board board)
            throws MalformedMoveException, IllegalMoveException, AmbiguousMoveException {
        // leading and trailing whitespace is ignored
        input = input.strip();
        String regex = "^((?<pawnQuiet>[a-h][1-8])|" +
                "(?<pawnCapture>[a-h]x[a-h][1-8])|" +
                "(?<regular>[KQRBN](?<startFile>[a-h]?)(?<startRank>[1-8]?)(?<capture>x?)[a-h][1-8])|" +
                "(?<prom>[a-h][18]=[QRBN])|" +
                "(?<promCapture>[a-h]x[a-h][18]=[QRBN])|" +
                "(?<castleK>O-O|0-0)|" +
                "(?<castleQ>O-O-O|0-0-0))(?<check>\\+?)(?<mate>#?)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw new MalformedMoveException("Malformed move: " + input);
        }

        // Note that if an | branch is unmatched, matcher.group returns null;
        // but if an optional field (with ?) is not present, matcher.group returns the empty string
        // since the empty string matches "(...)?"
        String pawnQuiet = matcher.group("pawnQuiet");
        String pawnCapture = matcher.group("pawnCapture");
        String regular = matcher.group("regular");
        String prom = matcher.group("prom");
        String promCapture = matcher.group("promCapture");
        boolean isCheck = !matcher.group("check").isEmpty();
        boolean isMate = !matcher.group("mate").isEmpty();

        Move proposedMove = null;
        if (matcher.group("castleK") != null) {
            proposedMove = new Move(board.whiteToMove() ? 'K' : 'k');
        } else if (matcher.group("castleQ") != null) {
            proposedMove = new Move(board.whiteToMove() ? 'Q' : 'q');
        } else if (prom != null) {
            int startCol = prom.charAt(0) - 'a';
            int endCol = startCol;
            int endRow = prom.charAt(1) - '1';
            int startRow = endRow == 0 ? 1 : 6;
            char promotion = endRow == 0 ? (char) (prom.charAt(3) - 'A' + 'a') : prom.charAt(3);
            proposedMove = new Move(startRow, startCol, endRow, endCol, promotion, false);
        } else if (promCapture != null) {
            int startCol = promCapture.charAt(0) - 'a';
            int endCol = promCapture.charAt(2) - 'a';
            int endRow = promCapture.charAt(3) - '1';
            int startRow = endRow == 0 ? 1 : 6;
            char promotion = endRow == 0 ? (char) (promCapture.charAt(5) - 'A' + 'a') : promCapture.charAt(5);
            proposedMove = new Move(startRow, startCol, endRow, endCol, promotion, true);
        } else if (pawnQuiet != null) {
            int startCol = pawnQuiet.charAt(0) - 'a';
            int endCol = startCol;
            int endRow = pawnQuiet.charAt(1) - '1';
            int startRow;
            int advance = board.whiteToMove() ? 1 : -1;
            if (board.getPieceAt(endRow - advance, startCol) == 0) {
                // only possibility is pawn pushing 2 squares
                startRow = endRow - 2 * advance;
            } else {
                startRow = endRow - advance;
            }
            proposedMove = new Move(startRow, startCol, endRow, endCol, false, false);
        } else if (pawnCapture != null) {
            int startCol = pawnCapture.charAt(0) - 'a';
            int endCol = pawnCapture.charAt(2) - 'a';
            int endRow = pawnCapture.charAt(3) - '1';
            int startRow = board.whiteToMove() ? endRow - 1 : endRow + 1;
            boolean enPassant = false;
            if (board.getPieceAt(endRow, endCol) == 0) {
                // can only be en passant
                enPassant = true;
            }
            proposedMove = new Move(startRow, startCol, endRow, endCol, enPassant, true);
        } else if (regular != null) {
            // Need to check for ambiguity
            char pieceType = board.whiteToMove() ? regular.charAt(0) : (char)(regular.charAt(0) - 'A' + 'a');
            int endRow = regular.charAt(regular.length() - 1) - '1';
            int endCol = regular.charAt(regular.length() - 2) - 'a';
            boolean isCapture = !matcher.group("capture").isEmpty();
            String startFile = matcher.group("startFile");
            String startRank = matcher.group("startRank");
            // We use -1 for "unspecified"
            int startRow = startFile.isEmpty() ? -1 : startFile.charAt(0) - 'a';
            int startCol = startRank.isEmpty() ? -1 : startRank.charAt(0) - '1';

            // Iterate through all legal moves
            for (Move move : board.getLegalMoves()) {
                if (move.moveType == Move.Type.REGULAR
                && move.getEndRow() == endRow && move.getEndCol() == endCol
                && (startRow == -1 || move.getStartRow() == startRow)
                && (startCol == -1 || move.getStartCol() == startCol)
                && board.getPieceAt(move.getStartRow(), move.getStartCol()) == pieceType
                && move.getIsCapture() == isCapture) {
                    // Found match
                    if (proposedMove == null) {
                        proposedMove = new Move(move);
                    } else {
                        // Multiple legal moves match the description
                        throw new AmbiguousMoveException("Ambiguous move: " + input);
                    }
                }
            }
            if (proposedMove == null) {
                throw new IllegalMoveException("Illegal move: " + input);
            }
        } else {
            assert false;
        }

        if (!board.isLegal(proposedMove)) {
            System.out.println(proposedMove);
            System.out.println(board.getLegalMoves());
            throw new IllegalMoveException("Illegal move: " + input);
        }

        boolean success = board.move(proposedMove);
        assert success;
        if (isMate && (board.getWinner() == 'u' || board.getWinner() == 'd')) {
            // Note that it's impossible for white to make the last move and the winner to be black (and vice versa)
            // so we only need to check for undetermined and draw
            board.undoLastMove();
            throw new IllegalMoveException("Move " + input + " claims to be checkmate but it isn't");
        }
        if (isCheck && !board.isInCheck()) {
            board.undoLastMove();
            throw new IllegalMoveException("Move " + input + " claims to be check but it isn't");
        }
        board.undoLastMove();

        return proposedMove;
    }
}
