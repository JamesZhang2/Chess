package controller;

import model.eval.MaterialEvaluator;
import model.player.HumanCLIPlayer;
import model.player.MinimaxAIPlayer;
import model.player.Player;
import model.player.RandomAIPlayer;

public class Main {
    public static void main(String[] args) {
        Player whitePlayer = new HumanCLIPlayer(true);
//        Player blackPlayer = new RandomAIPlayer(false);
        Player blackPlayer = new MinimaxAIPlayer(false, new MaterialEvaluator(), 1);
        GameController gameController = new GameController(whitePlayer, blackPlayer);
        gameController.startGame();
    }
}
