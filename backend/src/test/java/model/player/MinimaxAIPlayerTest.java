package model.player;

import model.Util;
import model.board.BitmapBoard;
import model.board.Board;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import model.eval.Evaluator;
import model.eval.MaterialEvaluator;
import model.eval.TrivialEvaluator;
import model.move.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxAIPlayerTest {
    @Test
    void mateIn1() throws IllegalBoardException, MalformedFENException {
        // Based on the spec for Evaluator.evaluate,
        // Minimax AI player should always find mate in 1 with depth 1,
        // as long as the implementation of the evaluator is correct!
        Evaluator[] evaluators = {
                new TrivialEvaluator(),
                new MaterialEvaluator()
                // add more evaluators here...
        };
        String[] whiteM1 = {
                "6k1/1q3ppp/8/8/8/8/8/3R2K1 w - - 0 1",
                "q3k3/4p3/4P3/4K1R1/8/8/8/8 w - - 0 1",
                "8/8/8/8/1B6/8/2K5/k1N5 w - - 0 1",
                "8/3R4/6pk/8/6PK/8/8/8 w - - 0 1",
                "8/8/pppppppK/NBBR1NRp/nbbrqnrP/PPPPPPPk/8/Q7 w - - 0 1", // https://www.stmintz.com/ccc/index.php?id=123825
                "n7/k1PK4/p7/8/8/8/8/1R6 w - - 0 1",
                "n1q5/k2PK3/r7/8/8/8/8/1R6 w - - 0 1",
                "4q1kq/6p1/6K1/4R3/8/8/8/8 w - - 0 1"
        };
        Move[] whiteM1Solutions = {
                Util.moveFromSquares("d1", "d8", false, false),
                Util.moveFromSquares("g5", "g8", false, false),
                Util.moveFromSquares("b4", "c3", false, false),
                Util.moveFromSquares("g4", "g5", false, false),
                Util.moveFromSquares("a1", "h1", false, false),
                Util.moveFromSquares("c7", "c8", 'N', false),
                Util.moveFromSquares("d7", "c8", 'N', true),
                Util.moveFromSquares("e5", "e8", false, true)
        };
        for (Evaluator evaluator : evaluators) {
            MinimaxAIPlayer player = new MinimaxAIPlayer(true, evaluator, 1);
            for (int i = 0; i < whiteM1.length; i++) {
                Board board = new BitmapBoard(whiteM1[i]);
                assertEquals(new Action(whiteM1Solutions[i]), player.play(board), "Didn't find mate for " + whiteM1[i]);
            }
        }

        String[] blackM1 = {
                "2r4k/8/8/8/8/8/5PPP/6KQ b - - 0 1",
                "7k/5QRp/5P1R/8/8/3r4/4r3/6K1 b - - 0 1",
                "3n2k1/8/4QPPP/6K1/3r4/8/5r1r/8 b - - 0 1",
                "4k2r/8/3bPKP1/4PPP1/8/8/8/8 b k - 0 1",
                "8/8/8/3b2k1/4pP2/6PP/6KP/4rr2 b - f3 0 1"
        };
        Move[] blackM1Solutions = {
                Util.moveFromSquares("c8", "c1", false, false),
                Util.moveFromSquares("d3", "d1", false, false),
                Util.moveFromSquares("d8", "e6", false, true),
                new Move('k'),
                Util.moveFromSquares("e4", "f3", true, true)
        };
        for (Evaluator evaluator : evaluators) {
            MinimaxAIPlayer player = new MinimaxAIPlayer(false, evaluator, 1);
            for (int i = 0; i < blackM1.length; i++) {
                Board board = new BitmapBoard(blackM1[i]);
                assertEquals(new Action(blackM1Solutions[i]), player.play(board), "Didn't find mate for " + blackM1[i]);
            }
        }
    }

    @Test
    void materialistic() throws IllegalBoardException, MalformedFENException {
        Evaluator evaluator = new MaterialEvaluator();

        String[] whitePuzzles1 = {
                "4k1q1/8/8/8/4K1R1/8/8/8 w - - 0 1",
                "4k2q/8/8/8/3BK3/3R4/8/8 w - - 0 1",
                "4kq2/7p/6NK/7R/8/8/8/8 w - - 0 1",
                "2r3qk/4N3/8/8/8/8/PP6/R6K w - - 0 1"
        };
        Move[] whiteSolutions1 = {
                Util.moveFromSquares("g4", "g8", false, true),
                Util.moveFromSquares("d4", "h8", false, true),
                Util.moveFromSquares("g6", "f8", false, true),
                Util.moveFromSquares("e7", "g8", false, true),
        };
        for (int depth = 1; depth <= 3; depth++) {
            Player player = new MinimaxAIPlayer(true, evaluator, depth);
            for (int i = 0; i < whitePuzzles1.length; i++) {
                Board board = new BitmapBoard(whitePuzzles1[i]);
                assertEquals(new Action(whiteSolutions1[i]), player.play(board), "Incorrect move for " + whitePuzzles1[i]);
//                System.out.printf("Player with depth %d Finished Puzzle %d\n", depth, i);
            }
        }

        String[] whitePuzzles3 = {
                "3q3k/8/8/6N1/8/8/7P/7K w - - 0 1"
        };
        Move[] whiteSolutions3 = {
                Util.moveFromSquares("g5", "f7", false, false)
        };

        Player whitePlayer3 = new MinimaxAIPlayer(true, evaluator, 3);
        for (int i = 0; i < whitePuzzles3.length; i++) {
            Board board = new BitmapBoard(whitePuzzles3[i]);
            assertEquals(new Action(whiteSolutions3[i]), whitePlayer3.play(board), "Incorrect move for " + whitePuzzles3[i]);
        }

        String[] blackPuzzles1 = {
                "5r1k/8/8/8/8/8/7K/5Q2 b - - 0 1"
        };
        Move[] blackSolutions1 = {
                Util.moveFromSquares("f8", "f1", false, true)
        };
        for (int depth = 1; depth <= 3; depth++) {
            Player player = new MinimaxAIPlayer(false, evaluator, depth);
            for (int i = 0; i < blackPuzzles1.length; i++) {
                Board board = new BitmapBoard(blackPuzzles1[i]);
                assertEquals(new Action(blackSolutions1[i]), player.play(board), "Incorrect move for " + blackPuzzles1[i]);
//                System.out.printf("Player with depth %d Finished Puzzle %d\n", depth, i);
            }
        }
    }
}