package model.eval;

import model.Util;
import model.board.Board;

/**
 * An evaluator based on a weighted sum of the following factors: material, piece location, pawn structure.
 */
public class WeightedEvaluator implements Evaluator {
    // weights
    private final double materialWt, locationWt, pawnStructWt;

    private final MaterialEvaluator materialEval;
    private final LocationEvaluator locationEval;
    private final PawnStructEvaluator pawnStructEval;

    public WeightedEvaluator() {
        this(1.0, 0.5, 1.0);
    }

    public WeightedEvaluator(double materialWt, double locationWt, double pawnStructWt) {
        materialEval = new MaterialEvaluator();
        locationEval = new LocationEvaluator();
        pawnStructEval = new PawnStructEvaluator();
        this.materialWt = materialWt;
        this.locationWt = locationWt;
        this.pawnStructWt = pawnStructWt;
    }

    @Override
    public double evaluate(Board board) {
        if (board.getWinner() == 'w') {
            return Util.MATE_EVAL;
        } else if (board.getWinner() == 'b') {
            return -Util.MATE_EVAL;
        } else if (board.getWinner() == 'd') {
            return 0;
        }
        return materialWt * materialEval.evaluate(board)
                + locationWt * locationEval.evaluate(board)
                + pawnStructWt * pawnStructEval.evaluate(board);
    }
}
