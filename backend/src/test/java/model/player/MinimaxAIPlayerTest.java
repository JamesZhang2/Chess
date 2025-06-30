package model.player;

import model.Util;
import model.board.*;
import model.eval.Evaluator;
import model.eval.MaterialEvaluator;
import model.eval.TrivialEvaluator;
import model.eval.WeightedEvaluator;
import model.move.Move;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxAIPlayerTest {
    /**
     * Positions to test evaluations.
     */
    public final String[] EVAL_TEST_POSITIONS = {
            Util.START_POS,
            Handicap.WHITE_QUEEN.startPos,
            Handicap.BLACK_G_KNIGHT.startPos,
            "r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 0 1",
            "8/8/4k3/7Q/4K3/8/8/8 w - - 0 1",
            "8/8/6k1/8/6K1/8/6p1/6N1 w - - 0 1",
            "r1bqkb1r/pppp1pPp/2n5/8/4P3/2P2N2/PP3PPP/RNBQKB1R w KQkq - 0 1",
            "5rk1/5ppp/8/8/8/8/5PPP/q4RK1 w - - 0 1",
            "1k3q1b/6P1/8/8/8/8/8/1K6 w - - 0 1",
            "1k3b1q/6P1/8/8/8/8/8/1K6 w - - 0 1",
            "7k/8/8/8/8/8/7n/6rK w - - 0 1",
            "7k/8/8/8/8/8/6n1/5rK1 w - - 0 1",
            "r3k2r/8/8/8/8/8/8/R3K2R w KQq - 0 1",
            "kb6/2P3n1/8/5N1p/3q2B1/4RK2/1Nrp2n1/6Qr w - - 0 1"
    };

    @Test
    void mateIn1() throws IllegalBoardException, MalformedFENException {
        // Based on the spec for Evaluator.evaluate,
        // Minimax AI player should always find mate in 1 with depth 1,
        // as long as the implementation of the evaluator is correct!
        Evaluator[] evaluators = {
                new TrivialEvaluator(),
                new WeightedEvaluator()
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

    /**
     * Ask the Minimax AI player with the given parameters to compute the best eval and move
     * for the test positions, and prints information about runtime.
     * depth will be from 1 to maxDepth, inclusive.
     * @return a list of depth -> (fen, evalMovePair) maps; can be used to compare with the results
     * of the runs with different parameters.
     */
    private List<Map<String, EvalMovePair>> runEvalTests(Evaluator evaluator, int maxDepth,
                                                   boolean enablePruning, boolean enableQuiesce, boolean enableTpnTable)
            throws IllegalBoardException, MalformedFENException {
        List<Map<String, EvalMovePair>> results = new ArrayList<>();  // depth -> (fen, eval)
        results.add(new HashMap<>());  // for depth 0, which is unused
        System.out.printf("Pruning: %b, Quiesce: %b, TpnTable: %b\n", enablePruning, enableQuiesce, enableTpnTable);
        for (int depth = 1; depth <= maxDepth; depth++) {
            results.add(new HashMap<>());
            System.out.println("Running on depth " + depth);
            long startTime = System.nanoTime();
            MinimaxAIPlayer player = new MinimaxAIPlayer(true, evaluator, depth, enablePruning, enableQuiesce, enableTpnTable);
            for (String position : EVAL_TEST_POSITIONS) {
                Board board = new BitmapBoard(position);
                EvalMovePair pair = player.getBestEvalMove(board);
                results.get(depth).put(position, pair);
            }
            long endTime = System.nanoTime();
            System.out.println("Time spent on depth " + depth + ": " + (endTime - startTime) / 1.0e6 + " ms");
            if (enableTpnTable) {
                System.out.printf("Tpn table stats: exact hit: %d, lower bound hit: %d, upper bound hit: %d\n",
                        player.getExactHit(), player.getLowerBoundHit(), player.getUpperBoundHit());
            }
        }
        return results;
    }

    /**
     * Tests that Minimax with or without alpha-beta pruning gives the same answer
     */
    @Test
    void testPruning() throws IllegalBoardException, MalformedFENException {
        List<Map<String, EvalMovePair>> results000 = runEvalTests(new WeightedEvaluator(), 4, false, false, false);
        System.out.println();
        List<Map<String, EvalMovePair>> results100 = runEvalTests(new WeightedEvaluator(), 5, true, false, false);
        System.out.println();

        for (int depth = 1; depth <= 4; depth++) {
            System.out.println("Testing equality of results 000 and results100 for depth " + depth);
            for (String fen : results000.get(depth).keySet()) {
                assertEquals(results000.get(depth).get(fen).eval(),
                        results100.get(depth).get(fen).eval(),
                        String.format("Mismatch on depth %d, fen %s: no pruning gives %s while pruning gives %s",
                                depth, fen, results000.get(depth).get(fen), results100.get(depth).get(fen)));
            }
        }
    }

    @Test
    void testQuiescence() throws IllegalBoardException, MalformedFENException {
        Evaluator evaluator = new MaterialEvaluator();
        Board board = new BitmapBoard("4k3/8/6p1/5b2/8/8/5Q2/4K3 w - - 0 1");
        // In this position, without quiescence search, Qxf5 would seem like a good idea at the horizon (depth 1).
        // However, with quiescence search, it's a terrible idea since black can capture back.

        MinimaxAIPlayer noQuiesce = new MinimaxAIPlayer(true, evaluator, 1, false, false, false);
        MinimaxAIPlayer withQuiesce = new MinimaxAIPlayer(true, evaluator, 1, false, true, false);
        System.out.println("Without quiescence search: " + noQuiesce.getBestEvalMove(board));
        System.out.println("With quiescence search: " + withQuiesce.getBestEvalMove(board));
        assertEquals(Util.moveFromSquares("f2", "f5", false, true), noQuiesce.getBestEvalMove(board).move());
        assertNotEquals(Util.moveFromSquares("f2", "f5", false, true), withQuiesce.getBestEvalMove(board).move());
    }

    /**
     * Tests the performance hit after enabling quiescence search
     */
    @Test
    void testQuiescencePerformance() throws IllegalBoardException, MalformedFENException {
        List<Map<String, EvalMovePair>> results100 = runEvalTests(new WeightedEvaluator(), 4, true, false, false);
        System.out.println();
        List<Map<String, EvalMovePair>> results110 = runEvalTests(new WeightedEvaluator(), 4, true, true, false);
    }

    /**
     * Tests that a Minimax player with or without transposition tables give the same answer (for exact).
     * For pruning, they might give different answers since the tpn table entry may have a different bound
     * than the bound we get from searching the tree.
     */
    @Test
    void testTpnTable() throws IllegalBoardException, MalformedFENException {
        List<Map<String, EvalMovePair>> results000 = runEvalTests(new WeightedEvaluator(), 4, false, false, false);
        System.out.println();
        List<Map<String, EvalMovePair>> results001 = runEvalTests(new WeightedEvaluator(), 4, false, false, true);
        System.out.println();

        for (int depth = 1; depth <= 4; depth++) {
            System.out.println("Testing equality of results000 and results001 for depth " + depth);
            for (String fen : results000.get(depth).keySet()) {
                assertEquals(results001.get(depth).get(fen).eval(),
                        results000.get(depth).get(fen).eval(),
                        String.format("Mismatch on depth %d, fen %s: no tpn table gives %s while using tpn table gives %s",
                                depth, fen, results000.get(depth).get(fen), results001.get(depth).get(fen)));
            }
        }
        System.out.println();

        List<Map<String, EvalMovePair>> results100 = runEvalTests(new WeightedEvaluator(), 5, true, false, false);
        System.out.println();
        List<Map<String, EvalMovePair>> results101 = runEvalTests(new WeightedEvaluator(), 5, true, false, true);
    }

    /**
     * Temporary test on some positions with different settings
     */
    @Test
    void tempTest() throws IllegalBoardException, MalformedFENException {
        MinimaxAIPlayer player = new MinimaxAIPlayer(true, new WeightedEvaluator(), 4, true, false, false);
        Board board = new BitmapBoard("r1bqkbnr/pppppppp/2n5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2");
        System.out.println(player.getBestEvalMove(board));
        System.out.println();

        MinimaxAIPlayer player2 = new MinimaxAIPlayer(false, new WeightedEvaluator(), 3, true, false, false);
        Board board2 = new BitmapBoard("r1bqkbnr/pppppppp/B1n5/8/4P3/8/PPPP1PPP/RNBQK1NR b KQkq - 2 2");
        System.out.println(player2.getBestEvalMove(board2));
    }
}