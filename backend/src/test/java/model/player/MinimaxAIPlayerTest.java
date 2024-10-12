package model.player;

import model.Util;
import model.board.BitmapBoard;
import model.board.Board;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import model.eval.Evaluator;
import model.eval.TrivialEvaluator;
import model.move.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxAIPlayerTest {
    @Test
    public void mateIn1() throws IllegalBoardException, MalformedFENException {
        // Based on the spec for Evaluator.evaluate,
        // Minimax AI player should always find mate in 1 with depth 1,
        // as long as the implementation of the evaluator is correct!
        Evaluator evaluator = new TrivialEvaluator();
        MinimaxAIPlayer whitePlayer = new MinimaxAIPlayer(true, evaluator, 1);
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
        for (int i = 0; i < whiteM1.length; i++) {
            Board board = new BitmapBoard(whiteM1[i]);
            assertEquals(new Action(whiteM1Solutions[i]), whitePlayer.play(board), "Didn't find mate for " + whiteM1[i]);
        }

        MinimaxAIPlayer blackPlayer = new MinimaxAIPlayer(false, evaluator, 1);
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
        for (int i = 0; i < blackM1.length; i++) {
            Board board = new BitmapBoard(blackM1[i]);
            assertEquals(new Action(blackM1Solutions[i]), blackPlayer.play(board), "Didn't find mate for " + whiteM1[i]);
        }
    }
}