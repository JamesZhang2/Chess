package controller;

import model.eval.MaterialEvaluator;
import model.eval.WeightedEvaluator;
import model.player.HumanCLIPlayer;
import model.player.MinimaxAIPlayer;
import model.player.Player;
import model.player.RandomAIPlayer;

public class CLIMain {
    public static void main(String[] args) {
//        Player whitePlayer = new HumanCLIPlayer(true);
        Player whitePlayer = new RandomAIPlayer(true);
        Player blackPlayer = new MinimaxAIPlayer(false, new WeightedEvaluator(), 3);
//        try {
//            CLIGameController gameController = new GameController(whitePlayer, blackPlayer, "4qk2/8/5K2/8/5R2/8/8/8 w - - 0 1");
//            gameController.startGame();
//        } catch (Exception ignored) {
//
//        }
         CLIGameController gameController = new CLIGameController(whitePlayer, blackPlayer);
         gameController.startGame();
    }
}
