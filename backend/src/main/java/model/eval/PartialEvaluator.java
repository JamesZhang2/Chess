package model.eval;

import model.board.Board;

/**
 * Components of the full evaluator.
 */
public interface PartialEvaluator {
    /**
     * Evaluates one aspect of the position.
     * @param board The board to evaluate
     * @return a partial evaluation of the position.
     * A positive eval means that white has an advantage.
     * A negative eval means that black has an advantage.
     * The bigger the absolute value, the larger the advantage.
     * Does not have to return (+infinity, 0, -infinity) for (white won, drawn, black won).
     * Postcondition: The state of the board is unchanged.
     */
    double evaluate(Board board);
}
