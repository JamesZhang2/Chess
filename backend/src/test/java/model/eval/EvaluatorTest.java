package model.eval;

import model.board.BitmapBoard;
import model.board.Board;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class EvaluatorTest {
    protected abstract Evaluator getEvaluator();

    @Test
    void testCompletedGames() throws IllegalBoardException, MalformedFENException {
        Evaluator evaluator = getEvaluator();
        final double DELTA = 0.001;
        String[] whiteWon = {
                "5k1R/8/5K2/8/8/8/8/8 b - - 1 1",
                "7k/6Q1/6K1/8/8/8/8/8 b - - 0 1",
                "6rk/4KNpq/7p/8/8/8/8/8 b - - 0 1"
        };
        String[] blackWon = {
                "rnb1kbnr/pppp1ppp/4p3/8/5PPq/8/PPPPP2P/RNBQKBNR w KQkq - 1 3",
                "7k/5Rpp/8/4B3/8/7Q/5PPP/3r2K1 w - - 0 1",
                "5n1K/5k2/5b2/8/8/8/8/8 w - - 8 5"
        };
        String[] draw = {
                "5k2/5P2/5K2/8/8/8/8/8 b - - 0 1",
                "7K/8/8/8/8/8/8/k7 w - - 0 1",
                "7K/7N/8/8/8/8/8/k7 w - - 0 1",
                "8/3BK3/8/8/3kb3/8/8/8 w - - 0 1",
                "6k1/8/6KQ/8/8/8/8/8 w - - 101 99",
                "5B2/8/8/8/8/8/2K5/k1N5 b - - 1 1"
        };
        for (String fen : whiteWon) {
            assertEquals(Double.POSITIVE_INFINITY, evaluator.evaluate(new BitmapBoard(fen)), DELTA);
        }
        for (String fen : blackWon) {
            assertEquals(Double.NEGATIVE_INFINITY, evaluator.evaluate(new BitmapBoard(fen)), DELTA);
        }
        for (String fen : draw) {
            assertEquals(0, evaluator.evaluate(new BitmapBoard(fen)), DELTA);
        }
    }
}