package controller;

import model.board.*;
import model.move.PGN;
import model.player.Player;

/**
 * The game controller sets up the board and asks the players to play.
 * Handles resign, offer draw, accept draw, and decline draw.
 */
public class GameController {
    protected final Board board;
    protected final Player whitePlayer, blackPlayer;

    /**
     * Initialize a game controller with the starting position.
     */
    public GameController(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = new BitmapBoard();
    }

    /**
     * Initialize a game controller with the given handicap.
     */
    public GameController(Player whitePlayer, Player blackPlayer, Handicap handicap) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = new BitmapBoard(handicap);
    }

    /**
     * Initialize a game controller with the position specified by the FEN.
     * @throws MalformedFENException if the FEN is malformed
     * @throws IllegalBoardException if the board parsed from the FEN is illegal
     */
    public GameController(Player whitePlayer, Player blackPlayer, String FEN)
            throws IllegalBoardException, MalformedFENException {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = new BitmapBoard(FEN);
    }

    public String getPGN() {
        return board.toPGN();
    }
}
