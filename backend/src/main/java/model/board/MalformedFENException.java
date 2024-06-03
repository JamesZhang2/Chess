package model.board;

/**
 * Thrown when the FEN is malformed.
 */
public class MalformedFENException extends Exception {
    public MalformedFENException(String message) {
        super(message);
    }
}
