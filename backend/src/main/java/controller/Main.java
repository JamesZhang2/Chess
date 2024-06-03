package controller;

import model.player.HumanCLIPlayer;
import model.player.Player;
import model.player.RandomAIPlayer;

public class Main {
    public static void main(String[] args) {
        Player whitePlayer = new HumanCLIPlayer(true);
        Player blackPlayer = new RandomAIPlayer(false);
        GameController gameController = new GameController(whitePlayer, blackPlayer);
        gameController.startGame();
    }
}
