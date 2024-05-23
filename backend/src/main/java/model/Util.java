package model;

/**
 * A class for global constants and utilities.
 */
public class Util {
    public static final String START_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static final char[] PIECE_NAMES = {'p', 'n', 'b', 'r', 'q', 'k', 'P', 'N', 'B', 'R', 'Q', 'K'};

    /**
     * @return true if input is in [0...7], false otherwise
     */
    public static boolean inRange(int input) {
        return input >= 0 && input <= 7;
    }

    /**
     * Returns the upper case of c.
     * Requires: c is either a lower-case or upper-case letter
     */
    public static char toUpperCase(char c) {
        return c >= 'a' ? (char) (c - 'a' + 'A') : c;
    }
}
