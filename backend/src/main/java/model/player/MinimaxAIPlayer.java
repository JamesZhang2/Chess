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
    private final Map<Long, TpnTableEntry> regularTpnTable;  // transposition table for regular moves
    private final Map<Long, TpnTableEntry> quiesceTpnTable;  // transposition table for quiescence (captures only)

    // Whether to enable alpha-beta pruning. Usually it's always true. Can be set to false when debugging or testing.
    // Reference: https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
    private final boolean ENABLE_PRUNING;

    // Whether to enable quiescence search. Usually it's always true. Can be set to false when debugging or testing.
    // Reference: https://www.chessprogramming.org/Quiescence_Search
    private final boolean ENABLE_QUIESCE;

    // Whether to use transposition tables. Usually it's always true. Can be set to false when debugging or testing.
    // Reference: https://www.chessprogramming.org/Transposition_Table
    private final boolean ENABLE_TPN_TABLE;

    private int exactHit = 0, lowerBoundHit = 0, upperBoundHit = 0;  // some tpn table stats to estimate performance

    /**
     * Constructs a new Minimax AI Player with pruning and quiescence search enabled.
     */
    public MinimaxAIPlayer(boolean isWhite, Evaluator evaluator, int maxDepth) {
        this(isWhite, evaluator, maxDepth, true, true, true);
    }

    /**
     * Constructs a new Minimax AI Player.
     *
     * @param isWhite   true if the player is playing white, false otherwise
     * @param evaluator the evaluator used for leaf positions
     * @param maxDepth  the maximum depth to run minimax. Requires: maxDepth >= 1.
     */
    public MinimaxAIPlayer(boolean isWhite, Evaluator evaluator, int maxDepth,
                           boolean enablePruning, boolean enableQuiesce, boolean enableTpnTable) {
        super(isWhite);
        this.evaluator = evaluator;
        this.MAX_DEPTH = maxDepth;
        this.ENABLE_PRUNING = enablePruning;
        this.ENABLE_QUIESCE = enableQuiesce;
        this.ENABLE_TPN_TABLE = enableTpnTable;
        this.regularTpnTable = new HashMap<>();
        this.quiesceTpnTable = new HashMap<>();
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
            double eval = evaluate(board, MAX_DEPTH - 1, alpha, beta, !isWhite);
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
     * @param board      The board to evaluate.
     * @param depth      The depth of the search. If zero, then we reached a leaf position.
     * @param alpha      The alpha value (lower bound - the maximizing player can guarantee this value or higher)
     * @param beta       The beta value (upper bound - the minimizing player can guarantee this value or lower)
     * @param maximizing true if we want to maximize, false otherwise
     * @return the eval
     * Postcondition: The state of the board is unchanged.
     */
    private double evaluate(Board board, int depth, double alpha, double beta, boolean maximizing) {
        if (board.getWinner() != 'u') {
            return evaluator.evaluate(board);
        }

        long zobristHash = board.getZobristHash();

        if (ENABLE_TPN_TABLE && regularTpnTable.containsKey(zobristHash)) {
            TpnTableEntry entry = regularTpnTable.get(zobristHash);
            if (entry.depth() >= depth) {
                if (entry.type() == TpnTableEntry.Type.EXACT) {
                    exactHit++;
                    return entry.eval();
                } else if (ENABLE_PRUNING && entry.type() == TpnTableEntry.Type.LOWER_BOUND
                        && maximizing && entry.eval() > beta + EPSILON) {
                    lowerBoundHit++;
                    return entry.eval();
                } else if (ENABLE_PRUNING && entry.type() == TpnTableEntry.Type.UPPER_BOUND
                    && !maximizing && entry.eval() < alpha - EPSILON) {
                    upperBoundHit++;
                    return entry.eval();
                }
            }
        }

        if (depth == 0) {
            // no more depth or game has ended, leaf node
            if (ENABLE_QUIESCE) {
                return quiesce(board, alpha, beta, maximizing);
            } else {
                return evaluator.evaluate(board);
            }
        }

        Move bestMove = null;
        if (maximizing) {
            // we're the maximizer, and we're trying to choose among the legal moves
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : board.getLegalMoves()) {
                board.move(move, false);
                // evaluate resulting board from opponent's point of view
                double eval = evaluate(board, depth - 1, alpha, beta, false);
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
                    // opponent (minimizer) can already guarantee beta,
                    // so they won't go down this entire branch.
                    if (ENABLE_TPN_TABLE) {
                        regularTpnTable.put(zobristHash,
                                new TpnTableEntry(zobristHash, TpnTableEntry.Type.LOWER_BOUND, eval, depth, null));
                    }
                    return eval;
                }
                alpha = Math.max(alpha, eval);  // tell siblings that the maximizer can achieve at least this alpha
            }
            if (ENABLE_TPN_TABLE) {
                regularTpnTable.put(zobristHash,
                        new TpnTableEntry(zobristHash, TpnTableEntry.Type.EXACT, maxEval, depth, bestMove));
            }
            return maxEval;
        } else {
            // we're the minimizer, and we're trying to choose among the legal moves
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : board.getLegalMoves()) {
                board.move(move, false);
                // evaluate resulting board from opponent's point of view
                double eval = evaluate(board, depth - 1, alpha, beta, true);
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
                    // opponent (maximizer) can already guarantee alpha,
                    // so they won't go down this entire branch.
                    if (ENABLE_TPN_TABLE) {
                        regularTpnTable.put(zobristHash,
                                new TpnTableEntry(zobristHash, TpnTableEntry.Type.UPPER_BOUND, eval, depth, null));
                    }
                    return eval;
                }
                beta = Math.min(beta, eval);  // tell siblings that the minimizer can achieve at most this beta
            }
            if (ENABLE_TPN_TABLE) {
                regularTpnTable.put(zobristHash,
                        new TpnTableEntry(zobristHash, TpnTableEntry.Type.EXACT, minEval, depth, bestMove));
            }
            return minEval;
        }
    }

    /**
     * Keep searching until we reach a quiet position, then evaluate.
     */
    private double quiesce(Board board, double alpha, double beta, boolean maximizing) {
        double staticEval = evaluator.evaluate(board);
        // only consider captures
        Set<Move> legalCaptures = board.getLegalMoves(true);
        if (legalCaptures.isEmpty()) {
            return staticEval;
        }

        if (maximizing) {
            // stand pat
            double maxEval = staticEval;
            if (maxEval >= beta) {
                return maxEval;
            }

            for (Move move : legalCaptures) {
                board.move(move, false);
                double eval = quiesce(board, alpha, beta, false);
                board.undoLastMove();

                if (ENABLE_PRUNING && eval > beta + EPSILON) {
                    return eval;
                }
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
            }
            return maxEval;
        } else {
            // stand pat
            double minEval = staticEval;
            if (minEval <= alpha) {
                return minEval;
            }

            for (Move move : legalCaptures) {
                board.move(move, false);
                double eval = quiesce(board, alpha, beta, true);
                board.undoLastMove();

                if (ENABLE_PRUNING && eval < alpha - EPSILON) {
                    return eval;
                }
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
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
        System.out.println(isWhite ? "White won" : "Black won");
    }

    public int getExactHit() {
        return exactHit;
    }

    public int getLowerBoundHit() {
        return lowerBoundHit;
    }

    public int getUpperBoundHit() {
        return upperBoundHit;
    }
}
