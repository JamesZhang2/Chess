package model.player;

import model.board.Board;
import model.eval.Evaluator;
import model.move.Move;

/**
 * An AI that plays chess using Minimax.
 */
public class MinimaxAIPlayer extends Player {
    private Evaluator evaluator;
    private final int MAX_DEPTH;

    // Whether to enable alpha-beta pruning. Usually it's always true. Can be set to false when debugging.
    private final boolean ENABLE_PRUNING = true;

    /**
     * Constructs a new Minimax AI Player.
     * @param isWhite true if the player is playing white, false otherwise
     * @param evaluator the evaluator used for leaf positions
     * @param maxDepth the maximum height to run minimax
     */
    public MinimaxAIPlayer(boolean isWhite, Evaluator evaluator, int maxDepth) {
        super(isWhite);
        this.evaluator = evaluator;
        this.MAX_DEPTH = maxDepth;
    }

    @Override
    public Action play(Board board) {
        Move bestMove = null;
        double bestEval = isWhite ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for (Move move : board.getLegalMoves()) {
            board.move(move);
            // evaluate resulting board from opponent's point of view
            double eval = evaluate(board, MAX_DEPTH, !isWhite);
            if (isWhite) {
                if (eval > bestEval) {
                    bestMove = move;
                    bestEval = eval;
                }
            } else {
                if (eval < bestEval) {
                    bestMove = move;
                    bestEval = eval;
                }
            }
            board.undoLastMove();
        }
        return new Action(bestMove);
    }

    /**
     * Evaluate the current position using minimax.
     * @param board The board to evaluate.
     * @param depth The depth of the search. If zero, then we reached a leaf position.
     * @param maximizing true if we want to maximize, false otherwise
     * @return the eval
     * Postcondition: The state of the board is unchanged.
     */
    private double evaluate(Board board, int depth, boolean maximizing) {
        if (depth == 0) {
            return evaluator.evaluate(board);
        }
        double bestEval = maximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for (Move move : board.getLegalMoves()) {
            board.move(move);
            // evaluate resulting board from opponent's point of view
            double eval = evaluate(board, depth - 1, !maximizing);
            if (maximizing) {
                if (eval > bestEval) {
                    bestEval = eval;
                }
            } else {
                if (eval < bestEval) {
                    bestEval = eval;
                }
            }
            board.undoLastMove();
        }
        return bestEval;
    }

    @Override
    public boolean considerDraw(Board board) {
        return false;
    }
}
