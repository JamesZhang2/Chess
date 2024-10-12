package model.eval;

import model.board.BitmapBoard;
import model.board.Board;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialEvaluatorTest extends EvaluatorTest {

    @Override
    protected Evaluator getEvaluator() {
        return new MaterialEvaluator();
    }

    @Test
    void testMaterialEvaluation() throws IllegalBoardException, MalformedFENException {
        Evaluator evaluator = new MaterialEvaluator();
        final double DELTA = 0.001;
        Board board1 = new BitmapBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(0, evaluator.evaluate(board1), DELTA);
        Board board2 = new BitmapBoard("rnb1kbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(MaterialEvaluator.QUEEN_VALUE, evaluator.evaluate(board2), DELTA);
        Board board3 = new BitmapBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1");
        assertEquals(-MaterialEvaluator.QUEEN_VALUE, evaluator.evaluate(board3), DELTA);
        Board board4 = new BitmapBoard("4k3/8/8/8/8/8/8/4K2R w - - 0 1");
        assertEquals(MaterialEvaluator.ROOK_VALUE, evaluator.evaluate(board4), DELTA);
        Board board5 = new BitmapBoard("8/7P/7K/8/8/8/p7/Nk6 b - - 0 1");
        assertEquals(MaterialEvaluator.KNIGHT_VALUE, evaluator.evaluate(board5), DELTA);
    }
}