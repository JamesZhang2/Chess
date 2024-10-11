package model.eval;

import model.board.Board;

/**
 * An evaluation based solely on material.
 */
public class MaterialEval implements Evaluation {
    private final double PAWN_VALUE = 1.0;
    private final double KNIGHT_VALUE = 3.0;
    private final double BISHOP_VALUE = 3.2;  // bishops are slightly better than knights
    private final double ROOK_VALUE = 5.0;
    private final double QUEEN_VALUE = 9.0;

    @Override
    public double evaluate(Board board) {
        if (board.getWinner() == 'w') {
            return Double.POSITIVE_INFINITY;
        } else if (board.getWinner() == 'b') {
            return Double.NEGATIVE_INFINITY;
        } else if (board.getWinner() == 'd') {
            return 0;
        }
        char[][] pieces = board.getPieces();
        double score = 0;
        for (char[] row : pieces) {
            for (char piece : row) {
                score += getPieceValue(piece);
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
