package model.eval;

import model.board.Board;

/**
 * Interface for evaluating chess positions.
 */
public interface Evaluation {
    /**
     * Evaluates the position.
     * @param board The board to evaluate
     * @return an eval of the position.
     * A positive eval means that white has an advantage.
     * A negative eval means that black has an advantage.
     * The bigger the absolute value, the larger the advantage.
     * If white won, the eval must be +infinity.
     * If black won, the eval must be -infinity.
     * If it's a draw, the eval must be 0.
     */
    double evaluate(Board board);
}
