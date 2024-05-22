package model;

/**
 * A class for global constants and utilities.
 */
public class Util {
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
