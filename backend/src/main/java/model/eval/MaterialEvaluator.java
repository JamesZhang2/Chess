package model.eval;

import model.board.Board;

/**
 * An evaluator for material.
 */
public class MaterialEvaluator implements Evaluator {
    public static final double PAWN_VALUE = 1.0;
    public static final double KNIGHT_VALUE = 3.0;
    public static final double BISHOP_VALUE = 3.2;  // bishops are slightly better than knights
    public static final double ROOK_VALUE = 5.0;
    public static final double QUEEN_VALUE = 9.0;

    @Override
    public double evaluate(Board board) {
        double score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                score += getPieceValue(board.getPieceAt(r, c));
            }
        }
        return score;
    }

    /**
     * @return The value of the piece if the piece is pawn, knight, bishop, rook, or queen, 0 otherwise.
     */
    private double getPieceValue(char piece) {
        double val = switch (Character.toLowerCase(piece)) {
            case 'p' -> PAWN_VALUE;
            case 'n' -> KNIGHT_VALUE;
            case 'b' -> BISHOP_VALUE;
            case 'r' -> ROOK_VALUE;
            case 'q' -> QUEEN_VALUE;
            default -> 0;
        };
        return piece >= 'A' && piece <= 'Z' ? val : -val;
    }
}
