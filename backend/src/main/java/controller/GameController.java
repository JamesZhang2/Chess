package controller;

import model.Board;
import model.Player;

/**
 * The controller sets up the board and asks the players to play.
 * Handles resign, offer draw, accept draw, and decline draw.
 */
public class GameController {
    private Board board;
    private Player playerWhite, playerBlack;

    public void startGame() {
        // TODO
        // Note: Must pass in a clone of the board (or FEN) to players to avoid tampering with current board state
    }
}
