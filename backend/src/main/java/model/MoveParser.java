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
     * @throws IllegalMoveException   if the input represents a malformed or illegal move
     * @throws AmbiguousMoveException if the input represents an ambiguous move
     */
    public static Move parse(String input, Board board)
            throws MalformedMoveException, IllegalMoveException, AmbiguousMoveException {
        // leading and trailing whitespace is ignored
        input = input.strip();
        String regex = "^((?<pawnQuiet>[a-h][1-8])|" +
                "(?<regular>([KQRBN]|[a-h])(?<startFile>[a-h]?)(?<startRank>[1-8]?)(?<capture>x?)[a-h][1-8])|" +
                "(?<prom>[a-h][18]=[QRBN])|" +
                "(?<promCapture>[a-h]x[a-h][18]=[QRBN])|" +
                "(?<castleK>O-O|0-0)|" +
                "(?<castleQ>O-O-O|0-0-0))(?<check>\\+?)(?<mate>#?)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw new MalformedMoveException("Malformed move: " + input);
        }
        String pawnQuiet = matcher.group("pawnQuiet");
        String regular = matcher.group("regular");
        String prom = matcher.group("prom");
        String promCapture = matcher.group("promCapture");
        boolean isCheck = !matcher.group("check").isEmpty();
        boolean isMate = !matcher.group("mate").isEmpty();

        Move proposedMove;
        if (!matcher.group("castleK").isEmpty()) {
            proposedMove = new Move('K');
        } else if (!matcher.group("castleQ").isEmpty()) {
            proposedMove = new Move('Q');
        } else if (!prom.isEmpty()) {
            int startCol = prom.charAt(0) - 'a';
            int endCol = startCol;
            int endRow = prom.charAt(1) - '1';
            int startRow = endRow == 0 ? 1 : 6;
            char promotion = endRow == 0 ? (char) (prom.charAt(3) - 'A' + 'a') : prom.charAt(3);
            proposedMove = new Move(startRow, startCol, endRow, endCol, promotion, false);
        } else if (!promCapture.isEmpty()) {
            int startCol = promCapture.charAt(0) - 'a';
            int endCol = promCapture.charAt(2) - 'a';
            int endRow = promCapture.charAt(3) - '1';
            int startRow = endRow == 0 ? 1 : 6;
            char promotion = endRow == 0 ? (char)(promCapture.charAt(5) - 'A' + 'a') : promCapture.charAt(5);
            proposedMove = new Move(startRow, startCol, endRow, endCol, promotion, true);
        } else if (!pawnQuiet.isEmpty()) {
            int startCol = pawnQuiet.charAt(0) - 'a';
            int endCol = startCol;
            int endRow = pawnQuiet.charAt(1) - '1';
            int startRow;
            int advance = board.whiteToMove ? 1 : -1;
            if (board.getPieceAt(startCol, endRow - advance) == 0) {
                // only possibility is pawn pushing 2 squares
                startRow = endRow - 2 * advance;
            } else {
                startRow = endRow - advance;
            }
            proposedMove = new Move(startRow, startCol, endRow, endCol, false, false);
        } else if (!regular.isEmpty()) {
            // TODO
        } else {
            assert false;
        }
    }
}