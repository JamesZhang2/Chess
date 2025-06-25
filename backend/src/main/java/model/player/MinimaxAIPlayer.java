package model.player;

import model.Util;
import model.board.Board;
import model.eval.Evaluator;
import model.move.Move;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * An AI that plays chess using Minimax.
 */
public class MinimaxAIPlayer extends Player {
    private final Evaluator evaluator;
    private final int MAX_DEPTH;
    private final double DRAW_CUTOFF = 1.0;  // will draw as black if eval is greater than draw cutoff; mirrored for white

    // TODO: Caching (don't use FEN: computing FEN takes longer than not caching)
    // TODO: Also, with alpha-beta pruning, caching needs to consider which bound it is

    // Whether to enable alpha-beta pruning. Usually it's always true. Can be set to false when debugging.
    private final boolean ENABLE_PRUNING;

    // Reference for alpha-beta pruning: https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning

    /**
     * Constructs a new Minimax AI Player with pruning enabled.
     */
    public MinimaxAIPlayer(boolean isWhite, Evaluator evaluator, int maxDepth) {
        this(isWhite, evaluator, maxDepth, true);
    }

    /**
     * Constructs a new Minimax AI Player.
     *
     * @param isWhite   true if the player is playing white, false otherwise
     * @param evaluator the evaluator used for leaf positions
     * @param maxDepth  the maximum depth to run minimax. Requires: maxDepth >= 1.
     */
    public MinimaxAIPlayer(boolean isWhite, Evaluator evaluator, int maxDepth, boolean enablePruning) {
        super(isWhite);
        this.evaluator = evaluator;
        this.MAX_DEPTH = maxDepth;
        ENABLE_PRUNING = enablePruning;
    }

    @Override
    public Action play(Board board) {
        EvalMovePair pair = getBestEvalMove(board);
        return new Action(pair.move());
    }

    /**
     * @return the best evaluation and the best move
     */
    public EvalMovePair getBestEvalMove(Board board) {
        Move bestMove = null;
        double bestEval = isWhite ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        Map<Double, Move> evalMap;
        if (isWhite) {
            evalMap = new TreeMap<>((a, b) -> Double.compare(b, a));  // sorted by eval in descending order
        } else {
            evalMap = new TreeMap<>(Double::compareTo);  // sorted by eval in ascending order
        }
        for (Move move : board.getLegalMoves()) {
            board.move(move);
            // evaluate resulting board from opponent's point of view
            double eval = evaluate(board, MAX_DEPTH - 1, alpha, beta, !isWhite);
            evalMap.put(eval, move);
            if (isWhite) {
                if (eval >= bestEval) {
                    bestMove = move;
                    bestEval = eval;
                }
                alpha = Math.max(alpha, eval);
            } else {
                if (eval <= bestEval) {
                    bestMove = move;
                    bestEval = eval;
                }
                beta = Math.min(beta, eval);
            }
            board.undoLastMove();
        }
        System.out.println(evalMap);
        System.out.println("Evaluation: " + bestEval);
        System.out.println("Minimax AI plays " + bestMove);
        return new EvalMovePair(bestEval, bestMove);
    }

    /**
     * Evaluate the current position using minimax.
     *
     * @param board      The board to evaluate.
     * @param depth      The depth of the search. If zero, then we reached a leaf position.
     * @param alpha      The alpha value (lower bound - the maximizing player can guarantee this value or higher)
     * @param beta       The beta value (upper bound - the minimizing player can guarantee this value or lower)
     * @param maximizing true if we want to maximize, false otherwise
     * @return the eval
     * Postcondition: The state of the board is unchanged.
     */
    private double evaluate(Board board, int depth, double alpha, double beta, boolean maximizing) {
        if (depth == 0 || board.getWinner() != 'u') {
            // no more depth or game has ended, leaf node
            return evaluator.evaluate(board);
        }
        if (maximizing) {
            // we're the maximizer, and we're trying to choose among the legal moves
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : board.getLegalMoves()) {
                board.move(move);
                // evaluate resulting board from opponent's point of view
                double eval = evaluate(board, depth - 1, alpha, beta, false);
                board.undoLastMove();

                if (eval > Util.MATE_EVAL / 2) {
                    // forced mate, prefer lower depth
                    eval--;
                }
                maxEval = Math.max(maxEval, eval);
                if (ENABLE_PRUNING && eval >= beta) {
                    // opponent (minimizer) can already guarantee beta,
                    // so they won't go down this entire branch.
                    break;
                }
                alpha = Math.max(alpha, eval);  // tell siblings that the maximizer can achieve at least this alpha
            }
            return maxEval;
        } else {
            // we're the minimizer, and we're trying to choose among the legal moves
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : board.getLegalMoves()) {
                board.move(move);
                // evaluate resulting board from opponent's point of view
                double eval = evaluate(board, depth - 1, alpha, beta, true);
                board.undoLastMove();

                if (eval < -Util.MATE_EVAL / 2) {
                    // forced mate, prefer lower depth
                    eval++;
                }
                minEval = Math.min(minEval, eval);
                if (ENABLE_PRUNING && eval <= alpha) {
                    // opponent (maximizer) can already guarantee alpha,
                    // so they won't go down this entire branch.
                    break;
                }
                beta = Math.min(beta, eval);  // tell siblings that the minimizer can achieve at most this beta
            }
            return minEval;
        }
    }

    @Override
    public boolean considerDraw(Board board) {
        if (isWhite) {
            return evaluate(board, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false) < -DRAW_CUTOFF;
        } else {
            return evaluate(board, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true) > DRAW_CUTOFF;
        }
    }

    public void win(Board board) {
        System.out.println("won");
    }
}
