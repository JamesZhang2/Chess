package model.player;

import model.board.Board;

/**
 * Represents a player of a chess game.
 */
public abstract class Player {
    protected final boolean isWhite;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * @return an action based on the given board.
     * Note that the player is allowed to change the board,
     * so the GameController must always make a copy of the board.
     */
    abstract public Action play(Board board);

    /**
     * Respond to opponent's draw offer based on the current board
     * It's still the opponent's turn to move.
     *
     * @return true if player accepts a draw, false if player rejects a draw
     */
    abstract public boolean considerDraw(Board board);

    /**
     * Initialization before the game
     */
    public void init(Board board) {
    }

    /**
     * Notify the player that they won
     */
    public void win(Board board) {
    }

    /**
     * Notify the player that they drew
     */
    public void draw(Board board) {
    }

    /**
     * Notify the player that they lost
     */
    public void lose(Board board) {
    }

    /**
     * Notify the player that the draw offer was accepted
     */
    public void drawAccepted() {}

    /**
     * Notify the player that the draw offer was declined
     */
    public void drawDeclined() {}
}