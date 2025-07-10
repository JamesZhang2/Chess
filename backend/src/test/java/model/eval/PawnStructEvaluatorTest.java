package model.eval;

import model.board.BitmapBoard;
import model.board.Board;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PawnStructEvaluatorTest {

    @Test
    void testPawnStructEvaluation() throws IllegalBoardException, MalformedFENException {
        Evaluator evaluator = new PawnStructEvaluator();
        final double DELTA = 0.001;
        Board board1 = new BitmapBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(0, evaluator.evaluate(board1), DELTA);
        Board board2 = new BitmapBoard("4k3/pppppppp/8/8/8/8/PPP1P1PP/4K3 w - - 0 1");
        assertEquals(-PawnStructEvaluator.ISOLATED_PAWN_PENALTY, evaluator.evaluate(board2), DELTA);
        Board board3 = new BitmapBoard("4k3/pppppppp/8/8/8/4P3/PPPPP1PP/4K3 w - - 0 1");
        assertEquals(-PawnStructEvaluator.DOUBLED_PAWN_PENALTY, evaluator.evaluate(board3), DELTA);
        Board board4 = new BitmapBoard("4k3/pppppppp/8/8/8/4P3/PPP1P1PP/4K3 w - - 0 1");
        assertEquals(-PawnStructEvaluator.DOUBLED_PAWN_PENALTY - 2 * PawnStructEvaluator.ISOLATED_PAWN_PENALTY,
                evaluator.evaluate(board4), DELTA);
        Board board5 = new BitmapBoard("4k3/ppp3pp/8/8/8/8/PPPPPPPP/4K3 w - - 0 1");
        assertEquals(PawnStructEvaluator.PASSED_PAWN_REWARD[1], evaluator.evaluate(board5), DELTA);
        Board board6 = new BitmapBoard("4k3/ppp3pp/4P3/8/8/8/PPPP1PPP/4K3 w - - 0 1");
        assertEquals(PawnStructEvaluator.PASSED_PAWN_REWARD[5], evaluator.evaluate(board6), DELTA);
        Board board7 = new BitmapBoard("rnbqkb1r/pppppp1p/7p/8/3P4/8/PPP1PPPP/RN1QKBNR w KQkq - 0 1");
        assertEquals(PawnStructEvaluator.DOUBLED_PAWN_PENALTY + 2 * PawnStructEvaluator.ISOLATED_PAWN_PENALTY,
                evaluator.evaluate(board7), DELTA);
        Board board8 = new BitmapBoard("4k3/8/8/8/8/6pp/8/4K3 w - - 0 1");
        assertEquals(-2 * PawnStructEvaluator.PASSED_PAWN_REWARD[5], evaluator.evaluate(board8));
        Board board9 = new BitmapBoard("4k3/8/8/4p3/8/3P4/3P4/4K3 w - - 0 1");
        assertEquals(-PawnStructEvaluator.DOUBLED_PAWN_PENALTY -PawnStructEvaluator.ISOLATED_PAWN_PENALTY,
                evaluator.evaluate(board9), DELTA);
        Board board10 = new BitmapBoard("4k3/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQ - 0 1");
        assertEquals(0, evaluator.evaluate(board10));
    }
}