package model.eval;

import model.board.Board;

/**
 * An evaluator based on a weighted sum of the following factors: material, piece location, pawn structure.
 */
public class WeightedEvaluator implements Evaluator {
    // weights
    private final double materialWt, locationWt, pawnStructWt;

    private final MaterialPartialEvaluator materialEval;
    private final LocationPartialEvaluator locationEval;
    private final PawnStructPartialEvaluator pawnStructEval;

    public WeightedEvaluator() {
        this(1.0, 1.0, 1.0);
    }

    public WeightedEvaluator(double materialWt, double locationWt, double pawnStructWt) {
        materialEval = new MaterialPartialEvaluator();
        locationEval = new LocationPartialEvaluator();
        pawnStructEval = new PawnStructPartialEvaluator();
        this.materialWt = materialWt;
        this.locationWt = locationWt;
        this.pawnStructWt = pawnStructWt;
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
        return materialWt * materialEval.evaluate(board)
                + locationWt * locationEval.evaluate(board)
                + pawnStructWt * pawnStructEval.evaluate(board);
    }
}
