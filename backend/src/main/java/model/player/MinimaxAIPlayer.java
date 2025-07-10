package model.player;

import model.Util;
import model.board.Board;
import model.eval.Evaluator;
import model.move.Move;

import java.util.*;

/**
 * An AI that plays chess using Minimax.
 */
public class MinimaxAIPlayer extends Player {
    private final Evaluator evaluator;
    private final int MAX_DEPTH;
    private final double DRAW_CUTOFF = 1.0;  // will draw as black if eval is greater than draw cutoff; mirrored for white
    private final double EPSILON = 0.001;  // small number so that we only prune the branch if eval < alpha - EPSILON
    // rather than just eval <= alpha (and similarly for beta)
    // Since we return eval if a branch is pruned (when the real value is actually "<= eval"),
    // this avoids the situation where the maximizer thinks that two options (like 3 vs. "<= 3") are equally good
    // when one of them can potentially be much worse than the returned eval.

    // Maps Zobrist keys to transposition table entries
    private final TpnTable regularTpnTable;  // transposition table for regular moves
    private final TpnTable quiesceTpnTable;  // transposition table for quiescence (captures only)

    // Whether to enable alpha-beta pruning. Usually it's always true. Can be set to false when debugging or testing.
    // Reference: https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
    private final boolean ENABLE_PRUNING;

    // Whether to enable quiescence search. Usually it's always true. Can be set to false when debugging or testing.
    // Reference: https://www.chessprogramming.org/Quiescence_Search
    private final boolean ENABLE_QUIESCE;
    // The max depth for quiescence search. Setting this to any value >= 30 will enable full quiescence search (till quiet postion)
    // since there can at most be 30 captures in a game.
    private final int QUIESCE_MAX_DEPTH;

    // Whether to use transposition tables. Usually it's always true. Can be set to false when debugging or testing.
    // Reference: https://www.chessprogramming.org/Transposition_Table
    private final boolean ENABLE_TPN_TABLE;

    private long exactHits = 0, lowerBoundHits = 0, upperBoundHits = 0;  // some tpn table stats to estimate performance

    /**
     * Constructs a new Minimax AI Player with pruning and quiescence search enabled.
     */
    public MinimaxAIPlayer(boolean isWhite, Evaluator evaluator, int maxDepth) {
        this(isWhite, evaluator, maxDepth, true, true, true, 30);
    }

    /**
     * Constructs a new Minimax AI Player.
     *
     * @param isWhite   true if the player is playing white, false otherwise
     * @param evaluator the evaluator used for leaf positions
     * @param maxDepth  the maximum depth to run minimax. Requires: maxDepth >= 1.
     */
    public MinimaxAIPlayer(boolean isWhite, Evaluator evaluator, int maxDepth,
                           boolean enablePruning, boolean enableQuiesce, boolean enableTpnTable,
                           int quiesceMaxDepth) {
        super(isWhite);
        this.evaluator = evaluator;
        this.MAX_DEPTH = maxDepth;
        this.ENABLE_PRUNING = enablePruning;
        this.ENABLE_QUIESCE = enableQuiesce;
        this.ENABLE_TPN_TABLE = enableTpnTable;
        if (enableTpnTable) {
            this.regularTpnTable = new TpnTable(5_000_000);
            this.quiesceTpnTable = new TpnTable(5_000_000);
        } else {
            this.regularTpnTable = null;
            this.quiesceTpnTable = null;
        }
        this.QUIESCE_MAX_DEPTH = quiesceMaxDepth;
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
        PriorityQueue<EvalMovePair> pq;  // used for debugging and getting evals for all legal moves
        if (isWhite) {
            pq = new PriorityQueue<>((a, b) -> Double.compare(b.eval(), a.eval()));  // sorted by eval in descending order
        } else {
            pq = new PriorityQueue<>((a, b) -> Double.compare(a.eval(), b.eval()));  // sorted by eval in ascending order
        }
        for (Move move : board.getLegalMoves()) {
            board.move(move, false);  // we already know that the move is legal
            // evaluate resulting board from opponent's point of view
            double eval = evaluate(board, MAX_DEPTH - 1, alpha, beta, !isWhite, false);
            pq.add(new EvalMovePair(eval, move));
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
//        while (!pq.isEmpty()) {
//            System.out.print(pq.poll() + " ");
//        }
//        System.out.println();
//        System.out.printf("%s evaluation: %s\n", isWhite ? "White" : "Black", bestEval);
//        System.out.printf("%s Minimax AI plays %s\n", isWhite ? "White" : "Black", bestMove);
        return new EvalMovePair(bestEval, bestMove);
    }

    /**
     * Evaluate the current position using minimax.
     *
     * @param board        The board to evaluate.
     * @param depth        The depth of the search. If zero, then we reached a leaf position.
     * @param alpha        The alpha value (lower bound - the maximizing player can guarantee this value or higher)
     * @param beta         The beta value (upper bound - the minimizing player can guarantee this value or lower)
     * @param maximizing   true if we want to maximize, false otherwise
     * @param capturesOnly if true, only considers captures (quiescence search)
     * @return the eval
     * Postcondition: The state of the board is unchanged.
     */
    private double evaluate(Board board, int depth, double alpha, double beta, boolean maximizing, boolean capturesOnly) {
        if (board.getWinner() != 'u') {
            return evaluator.evaluate(board);
        }

        // which transposition table to use
        TpnTable tpnTable = capturesOnly ? quiesceTpnTable : regularTpnTable;

        long zobristHash = board.getZobristHash();
        if (ENABLE_TPN_TABLE) {
            Double entry = evalFromTpnTable(tpnTable, zobristHash, depth, alpha, beta, maximizing);
            if (entry != null) return entry;
        }

        if (depth == 0) {
            if (capturesOnly) {
                return evaluator.evaluate(board);
            } else {
                if (ENABLE_QUIESCE) {
                    return evaluate(board, QUIESCE_MAX_DEPTH, alpha, beta, maximizing, true);
                } else {
                    return evaluator.evaluate(board);
                }
            }
        }

        Move bestMove = null;
        if (maximizing) {
            // we're the maximizer, and we're trying to choose among the legal moves
            // if capturesOnly (quiescence search mode), we start with the stand pat value
            double maxEval = capturesOnly ? evaluator.evaluate(board) : Double.NEGATIVE_INFINITY;
            if (ENABLE_PRUNING && maxEval > beta + EPSILON) {
                // opponent (minimizer) can already guarantee beta,
                // so they won't go down this entire branch.
                if (ENABLE_TPN_TABLE) {
                    tpnTable.put(zobristHash,
                            new TpnTable.Entry(zobristHash, TpnTable.Entry.Type.LOWER_BOUND, maxEval, depth, null));
                }
                return maxEval;
            }

            for (Move move : board.getLegalMoves(capturesOnly)) {
                board.move(move, false);
                // evaluate resulting board from opponent's point of view
                double eval = evaluate(board, depth - 1, alpha, beta, false, capturesOnly);
                board.undoLastMove();

                if (eval > Util.MATE_EVAL / 2) {
                    // forced mate, prefer lower depth
                    eval--;
                }

                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                if (ENABLE_PRUNING && eval > beta + EPSILON) {
                    if (ENABLE_TPN_TABLE) {
                        tpnTable.put(zobristHash,
                                new TpnTable.Entry(zobristHash, TpnTable.Entry.Type.LOWER_BOUND, eval, depth, null));
                    }
                    return eval;
                }
                alpha = Math.max(alpha, eval);  // tell siblings that the maximizer can achieve at least this alpha
            }
            if (ENABLE_TPN_TABLE) {
                tpnTable.put(zobristHash,
                        new TpnTable.Entry(zobristHash, TpnTable.Entry.Type.EXACT, maxEval, depth, bestMove));
            }
            return maxEval;
        } else {
            // we're the minimizer, and we're trying to choose among the legal moves
            double minEval = capturesOnly ? evaluator.evaluate(board) : Double.POSITIVE_INFINITY;
            if (ENABLE_PRUNING && minEval < alpha - EPSILON) {
                // opponent (maximizer) can already guarantee alpha,
                // so they won't go down this entire branch.
                if (ENABLE_TPN_TABLE) {
                    tpnTable.put(zobristHash,
                            new TpnTable.Entry(zobristHash, TpnTable.Entry.Type.UPPER_BOUND, minEval, depth, null));
                }
                return minEval;
            }

            for (Move move : board.getLegalMoves(capturesOnly)) {
                board.move(move, false);
                // evaluate resulting board from opponent's point of view
                double eval = evaluate(board, depth - 1, alpha, beta, true,capturesOnly);
                board.undoLastMove();

                if (eval < -Util.MATE_EVAL / 2) {
                    // forced mate, prefer lower depth
                    eval++;
                }
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                if (ENABLE_PRUNING && eval < alpha - EPSILON) {
                    if (ENABLE_TPN_TABLE) {
                        tpnTable.put(zobristHash,
                                new TpnTable.Entry(zobristHash, TpnTable.Entry.Type.UPPER_BOUND, eval, depth, null));
                    }
                    return eval;
                }
                beta = Math.min(beta, eval);  // tell siblings that the minimizer can achieve at most this beta
            }
            if (ENABLE_TPN_TABLE) {
                tpnTable.put(zobristHash,
                        new TpnTable.Entry(zobristHash, TpnTable.Entry.Type.EXACT, minEval, depth, bestMove));
            }
            return minEval;
        }
    }

    /**
     * @param tpnTable the transposition table to use
     * @return the eval from the transposition table if it can be used; otherwise return null
     */
    private Double evalFromTpnTable(TpnTable tpnTable, long zobristHash, int depth, double alpha, double beta, boolean maximizing) {
        TpnTable.Entry entry = tpnTable.get(zobristHash);
        if (entry != null) {
            if (entry.depth() >= depth) {
                if (entry.type() == TpnTable.Entry.Type.EXACT) {
                    exactHits++;
                    return entry.eval();
                } else if (ENABLE_PRUNING && entry.type() == TpnTable.Entry.Type.LOWER_BOUND
                        && maximizing && entry.eval() > beta + EPSILON) {
                    lowerBoundHits++;
                    return entry.eval();
                } else if (ENABLE_PRUNING && entry.type() == TpnTable.Entry.Type.UPPER_BOUND
                        && !maximizing && entry.eval() < alpha - EPSILON) {
                    upperBoundHits++;
                    return entry.eval();
                }
            }
        }
        return null;
    }

    @Override
    public boolean considerDraw(Board board) {
        if (isWhite) {
            return evaluate(board, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, false) < -DRAW_CUTOFF;
        } else {
            return evaluate(board, MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, false) > DRAW_CUTOFF;
        }
    }

    public void win(Board board) {
        System.out.println(isWhite ? "White won" : "Black won");
    }

    public long getExactHits() {
        return exactHits;
    }

    public long getLowerBoundHits() {
        return lowerBoundHits;
    }

    public long getUpperBoundHits() {
        return upperBoundHits;
    }
}
