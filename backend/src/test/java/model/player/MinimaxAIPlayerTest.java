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
        MinimaxAIPlayer blackPlayer = new MinimaxAIPlayer(true, evaluator, 1);
        String[] whiteM1 = new String[]{
//                "6k1/1q3ppp/8/8/8/8/8/3R2K1 w - - 0 1",
//                "q3k3/4p3/4P3/4K1R1/8/8/8/8 w - - 0 1",
                "8/8/8/8/1B6/8/2K5/k1N5 w - - 0 1",
                "8/3R4/6pk/8/6PK/8/8/8 w - - 0 1",
                "8/8/pppppppK/NBBR1NRp/nbbrqnrP/PPPPPPPk/8/Q7 w - - 0 1", // https://www.stmintz.com/ccc/index.php?id=123825
                "n7/k1PK4/p7/8/8/8/8/1R6 w - - 0 1",
                "n1q5/k2PK3/r7/8/8/8/8/1R6 w - - 0 1",
                "4q1kq/6p1/6K1/4R3/8/8/8/8 w - - 0 1"
        };
        Move[] whiteM1Solutions = new Move[]{
//                Util.moveFromSquares("d1", "d8", false, false),
//                Util.moveFromSquares("g5", "g8", false, false),
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
    }
}