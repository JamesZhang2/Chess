package model;

import java.util.Set;

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
     * <p>
     * @return the move parsed from the input string, given the current board state
     * @throws IllegalMoveException if the input represents a malformed or illegal move
     * @throws AmbiguousMoveException if the input represents an ambiguous move
     */
    public static Move parse(String input, Board board) throws IllegalMoveException, AmbiguousMoveException {
        // leading and trailing whitespace is ignored
        input = input.strip();
        if (input.length() < 2) {
            throw new IllegalMoveException("Malformed move: " + input);
        }

        // Take note of + or # and remove them
        int plusIdx = input.indexOf('+');
        int hashIdx = input.indexOf('#');
        boolean isCheck = false, isMate = false;
        if (plusIdx >= 0) {
            if (plusIdx == input.length() - 1) {
                isCheck = true;
                input = input.substring(0, input.length() - 1);
            } else {
                throw new IllegalMoveException("Malformed move: " + input);
            }
        }
        if (hashIdx >= 0) {
            if (hashIdx == input.length() - 1) {
                isMate = true;
                input = input.substring(0, input.length() - 1);
            } else {
                throw new IllegalMoveException("Malformed move: " + input);
            }
        }

        Move proposedMove;
        if (input.equals("O-O") || input.equals("0-0")) {
            proposedMove = new Move('K');
        } else if (input.equals("O-O-O") || input.equals("0-0-0")) {
            proposedMove = new Move('Q');
        }

        char startFile, startRank, endFile, endRank;
        int xIdx = input.indexOf('x');
        boolean isCapture = (xIdx >= 0);
        if (isCapture) {
            if (xIdx >= input.length() - 2) {
                // need at least two more chars after x
                throw new IllegalMoveException("Malformed move: " + input);
            }
        }


        int eqIdx = input.indexOf('=');
        if (eqIdx >= 0) {
            // Maybe promotion
            if (eqIdx != input.length() - 2) {
                throw new IllegalMoveException("Malformed move: " + input);
            }
            char proposedProm = input.charAt(input.length() - 1);
            if (proposedProm != 'Q' && proposedProm != 'R' && proposedProm != 'B' && proposedProm != 'N') {
                throw new IllegalMoveException("Unknown promotion type: " + proposedProm);
            }
        }

        Set<Move> legalMoves = board.getLegalMoves();
        return null;
    }
}
