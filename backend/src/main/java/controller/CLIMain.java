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
//        Player whitePlayer = new RandomAIPlayer(true);
        Player whitePlayer = new MinimaxAIPlayer(true, new WeightedEvaluator(1, 0.5, 0.9), 3);
        Player blackPlayer = new MinimaxAIPlayer(false, new WeightedEvaluator(1, 0.5, 0), 3);
//        try {
//            CLIGameController gameController = new GameController(whitePlayer, blackPlayer, "4qk2/8/5K2/8/5R2/8/8/8 w - - 0 1");
//            gameController.startGame();
//        } catch (Exception ignored) {
//
//        }
        try {
            CLIGameController gameController = new CLIGameController(whitePlayer, blackPlayer, "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2");
            gameController.startGame();
            System.out.println(gameController.getPGN());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
