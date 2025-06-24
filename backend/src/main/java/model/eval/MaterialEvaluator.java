package model.eval;

import model.board.Board;

/**
 * An evaluator based solely on material.
 */
public class MaterialEvaluator implements Evaluator {
    private final MaterialPartialEvaluator materialEval;

    public MaterialEvaluator() {
        materialEval = new MaterialPartialEvaluator();
    }

    @Override
    public double evaluate(Board board) {
        if (board.getWinner() == 'w') {
            return Double.POSITIVE_INFINITY;
        } else if (board.getWinner() == 'b') {
            return Double.NEGATIVE_INFINITY;
        } else if (board.getWinner() == 'd') {
            return 0;
        }
        return materialEval.evaluate(board);
    }
}
