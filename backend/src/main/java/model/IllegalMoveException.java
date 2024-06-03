package model;

/**
 * Thrown when the move parsed by the move parser is malformed or illegal.
 */
public class IllegalMoveException extends Exception {
    public IllegalMoveException(String message) {
        super(message);
    }
}
