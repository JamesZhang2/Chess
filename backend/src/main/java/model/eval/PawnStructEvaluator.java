package model.eval;

import model.Util;
import model.board.Board;
import model.board.Lookup;

/**
 * Evaluates a position based on pawn structures.
 */
public class PawnStructEvaluator implements Evaluator {

    public static final double DOUBLED_PAWN_PENALTY = 0.2;
    public static final double ISOLATED_PAWN_PENALTY = 0.25;
    public static final double[] PASSED_PAWN_REWARD = {0, 0.15, 0.2, 0.35, 0.5, 0.6, 0.9, 0};  // depending on rank, from white's perspective

    @Override
    public double evaluate(Board board) {
        long whitePawns = board.getBitmap('P');
        long blackPawns = board.getBitmap('p');
        double eval = 0;

        long whiteTemp = whitePawns;
        while (whiteTemp != 0) {
            int idx = Util.getLS1BIdx(whiteTemp);
            // isolated pawns
            if ((Lookup.ISOLATED_PAWN_MASK[idx] & whitePawns) == 0) {
                eval -= ISOLATED_PAWN_PENALTY;
//                System.out.println("isolated white pawn at " + Util.indexToSquare(idx));
            }
            // doubled pawns (only penalize the pawns at the back, don't penalize the pawn at the front)
            if ((Lookup.WHITE_DOUBLED_PAWN_MASK[idx] & whitePawns) != 0) {
                eval -= DOUBLED_PAWN_PENALTY;
//                System.out.println("doubled white pawn at " + Util.indexToSquare(idx));
            } else {
                // passed pawns (if doubled, only count passed pawns for the front one)
                if ((Lookup.WHITE_PASSED_PAWN_MASK[idx] & blackPawns) == 0) {
                    eval += PASSED_PAWN_REWARD[idx / 8];
//                    System.out.println("passed white pawn at " + Util.indexToSquare(idx));
                }
            }
            whiteTemp = Util.resetLS1B(whiteTemp);
        }

        long blackTemp = blackPawns;
        while (blackTemp != 0) {
            int idx = Util.getLS1BIdx(blackTemp);
            // isolated pawns
            if ((Lookup.ISOLATED_PAWN_MASK[idx] & blackPawns) == 0) {
                eval += ISOLATED_PAWN_PENALTY;
            }
            // doubled pawns (only penalize the pawns at the back, don't penalize the pawn at the front)
            if ((Lookup.BLACK_DOUBLED_PAWN_MASK[idx] & blackPawns) != 0) {
                eval += DOUBLED_PAWN_PENALTY;
            } else {
                // passed pawns (if doubled, only count passed pawns for the front one)
                if ((Lookup.BLACK_PASSED_PAWN_MASK[idx] & whitePawns) == 0) {
                    eval -= PASSED_PAWN_REWARD[7 - idx / 8];
                }
            }
            blackTemp = Util.resetLS1B(blackTemp);
        }

        return eval;
    }
}
