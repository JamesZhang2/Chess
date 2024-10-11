package model.eval;

import model.board.Board;

/**
 * A trivial evaluation of the board:
 * If white won, return +infinity. If black won, return -infinity. Otherwise, return 0
 */
public class TrivialEval implements Evaluation {
    @Override
    public double evaluate(Board board) {
        if (board.getWinner() == 'w') {
            return Double.POSITIVE_INFINITY;
        } else if (board.getWinner() == 'b') {
            return Double.NEGATIVE_INFINITY;
        }
        return 0;
    }
}
