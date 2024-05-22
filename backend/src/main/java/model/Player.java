package model;

/**
 * Represents a player of a chess game.
 */
public abstract class Player {
    private char color;

    /**
     * Make a move on the given board
     */
    abstract public Move play(MailboxBoard board);
}
