package model;

/**
 * Thrown when the board state is illegal.
 */
public class IllegalBoardException extends Exception {
    public IllegalBoardException(String message) {
        super(message);
    }
}
