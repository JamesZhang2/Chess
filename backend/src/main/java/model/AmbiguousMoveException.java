package model;

/**
 * Thrown when the move parsed by the move parser is ambiguous.
 */
public class AmbiguousMoveException extends Exception {
    public AmbiguousMoveException(String message) {
        super(message);
    }
}
