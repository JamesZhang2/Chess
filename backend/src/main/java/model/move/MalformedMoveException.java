package model.move;

/**
 * Thrown when the move parsed by the move parser is malformed.
 */
public class MalformedMoveException extends Exception {
    public MalformedMoveException(String message) {
        super(message);
    }
}
