package model.eval;

import model.board.Board;

/**
 * An evaluator for piece locations
 */
public class LocationEvaluator implements Evaluator {
    // Reference: https://www.chessprogramming.org/Piece-Square_Tables

    // how much each piece contributes to the phase weight
    private final int Q_PHASE_WT = 4;
    private final int R_PHASE_WT = 2;
    private final int B_PHASE_WT = 1;
    private final int N_PHASE_WT = 1;
    private final int TOTAL_WT = 24;

    // Piece square tables, in centipawns
    // MG: early/middle game
    // EG: endgame
    // Since (r, c) = (0, 0) represents a1, these tables visually look like they are from black's perspective,
    // but they're actually from white's perspective.

    // center pawns are more valuable than side pawns.
    // pawns near the castled king shouldn't move too much;
    // center pawns should move; promotion should be encouraged
    private final int[][] MG_P_TABLE = {
            {  0,  0,  0,  0,  0,  0,  0,  0},
            {  5, 10, 10,-25,-25, 10, 10,  5},
            {  2,  5,-10, 10, 10,-10,  5,  2},
            {-10,-10,  0, 25, 25,  0,-10,-10},
            {  0,  0, 10, 30, 30, 10,  0,  0},
            {  5, 15, 25, 40, 40, 25, 15,  5},
            { 40, 50, 60, 60, 60, 60, 50, 40},
            {  0,  0,  0,  0,  0,  0,  0,  0},
    };

    // in the endgame, flank pawns do not need to stay back to protect the king.
    // they should be encouraged to push forward to promote
    private final int[][] EG_P_TABLE = {
            {  0,  0,  0,  0,  0,  0,  0,  0},
            {-10, -5, -2,  0,  0, -2, -5,-10},
            {  2,  8, 10, 12, 12, 10,  8,  2},
            { 12, 20, 30, 35, 35, 30, 20, 12},
            { 30, 40, 50, 50, 50, 50, 40, 30},
            { 50, 60, 70, 70, 70, 70, 60, 50},
            { 90,100,100,100,100,100,100, 90},
            {  0,  0,  0,  0,  0,  0,  0,  0},
    };

    // Knights and bishops should be encouraged to move towards the center, especially in the early game.
    // Knights at the edges and corners get a more severe punishment than bishops
    // since knights are short-range pieces.
    private final int[][] MG_N_TABLE = {
            {-85,-60,-50,-50,-50,-50,-60,-85},
            {-60,-30,-10,  0,  0,-10,-30,-60},
            {-50,  5, 10, 15, 15, 10, 10,-50},
            {-50,  0, 15, 20, 20, 15,  0,-50},
            {-50,  0, 20, 25, 25, 20,  0,-50},
            {-50,-10, 10, 15, 15, 10,-10,-50},
            {-60,-30,-10,  0,  0,-10,-30,-60},
            {-85,-60,-50,-50,-50,-50,-60,-85},
    };

    private final int[][] EG_N_TABLE = {
            {-75,-50,-40,-40,-40,-40,-50,-75},
            {-50,-20, -5,  0,  0, -5,-20,-50},
            {-40, -5, 10, 15, 15, 10, -5,-40},
            {-40,  0, 20, 30, 30, 20,  0,-40},
            {-40,  0, 20, 30, 30, 20,  0,-40},
            {-40, -5, 10, 15, 15, 10, -5,-40},
            {-50,-20, -5,  0,  0, -5,-20,-50},
            {-75,-50,-40,-40,-40,-40,-50,-75},
    };

    private final int[][] MG_B_TABLE = {
            {-30,-15,-15,-15,-15,-15,-15,-30},
            {-15, 10,  0,  0,  0,  0, 10,-15},
            {-15, 15, 15, 15, 15, 15, 15,-15},
            {-15,  0, 20, 20, 20, 20,  0,-15},
            {-15,  5, 10, 10, 10, 10,  5,-15},
            {-15,  0, 10, 10, 10, 10,  0,-15},
            {-15,  0,  0,  0,  0,  0,  0,-15},
            {-30,-15,-15,-15,-15,-15,-15,-30},
    };

    private final int[][] EG_B_TABLE = {
            {-30,-15,-15,-15,-15,-15,-15,-30},
            {-15,  0,  0,  0,  0,  0,  0,-15},
            {-15,  0, 10, 10, 10, 10,  0,-15},
            {-15,  0, 10, 15, 15, 10,  0,-15},
            {-15,  0, 10, 15, 15, 10,  0,-15},
            {-15,  0, 10, 10, 10, 10,  0,-15},
            {-15,  0,  0,  0,  0,  0,  0,-15},
            {-30,-15,-15,-15,-15,-15,-15,-30},
    };

    // Rooks on the 7th rank are great. Centralizing rooks is also a good idea.
    // Lifting rooks through a3/h3 is a bad idea.
    private final int[][] MG_R_TABLE = {
            {-25,-15, 10, 15, 15, 10,-15,-25},
            {-30, -5, -5, -5, -5, -5, -5,-30},
            {-35,  0,  0,  0,  0,  0,  0,-35},
            {-30,  0,  0,  0,  0,  0,  0,-30},
            {-20,  0,  0,  0,  0,  0,  0,-20},
            {-10,  5,  5,  5,  5,  5,  5,-10},
            { 20, 30, 30, 30, 30, 30, 30, 20},
            { 10, 15, 15, 15, 15, 15, 15, 10},
    };

    // Rooks on the 7th rank are great. Centralizing rooks is also a good idea.
    // Lifting rooks through a3/h3 is a bad idea.
    private final int[][] EG_R_TABLE = {
            {-25,  0,  0,  0,  0,  0,  0,-25},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  5,  5,  5,  5,  5,  5, -5},
            {  5, 10, 10, 10, 10, 10, 10,  5},
            {  5,  5,  5,  5,  5,  5,  5,  5},
    };

    // The queen should not move much in the early game since it is likely going to be a target.
    // b7 is often a poisoned pawn.
    private final int[][] MG_Q_TABLE = {
            {-15,-15,-10, 10,-10,-15,-20,-25},
            {-10,  0, 10,  5,  5,  5,  0,-10},
            {-10,  5,  0,  0,  0,  0,  0,-10},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,-15,  0,  0,  0,  0,  0,-10},
            {-25,-10,-10, -5, -5,-10,-10,-25},
    };

    // In the end game the queen should be doing damage in the center or on the enemy side.
    private final int[][] EG_Q_TABLE = {
            {-25,-20,-15,-15,-15,-15,-20,-25},
            {-10, -5, -5, -5, -5, -5, -5,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            { -5, 10, 15, 20, 20, 15, 10, -5},
            { -5, 10, 15, 20, 20, 15, 10, -5},
            { -5,  5,  5, 10, 10,  5,  5, -5},
            { -5, 15, 20, 20, 20, 20, 15, -5},
            {  0, 10, 15, 15, 15, 15, 10,  0},
    };

    // In the early game, the king should stay sheltered behind pawns. Castling should be encouraged.
    // Moving the king to f1 without castling should be discouraged.
    private final int[][] MG_K_TABLE = {
            { 25, 45, 15,  0,  0,-20, 45, 25},
            { 25, 20, -5,-20,-20, -5, 20, 25},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {-30,-40,-40,-40,-40,-40,-40,-30},
            {-50,-50,-50,-50,-50,-50,-50,-50},
            {-50,-50,-50,-50,-50,-50,-50,-50},
            {-50,-50,-50,-50,-50,-50,-50,-50},
            {-50,-50,-50,-50,-50,-50,-50,-50},
    };

    // In the endgame, the king should fight for the center.
    private final int[][] EG_K_TABLE = {
            {-50,-40,-40,-40,-40,-40,-40,-50},
            {-40,-10,  0,  0,  0,  0,-10,-40},
            {-35,  0, 20, 30, 30, 20,  0,-35},
            {-35,  0, 30, 45, 45, 30,  0,-35},
            {-35,  0, 30, 45, 45, 30,  0,-35},
            {-35, 10, 25, 35, 35, 25, 10,-35},
            {-35,  5, 10, 10, 10, 10,  5,-35},
            {-50,-35,-35,-35,-35,-35,-35,-50},
    };

    // TODO: Incorporate castling status. It's more important for the pawns near the castled king to not move
    // TODO: than the ones away from the castled king.

    @Override
    public double evaluate(Board board) {
        double phase = getGamePhase(board);
        double centipawns = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char piece = board.getPieceAt(r, c);
                if (piece == 0) {
                    continue;
                }
                boolean isWhite = Character.isUpperCase(piece);
                centipawns += (isWhite ? 1 : -1) * switch (piece) {
                    case 'P', 'p' -> phase * MG_P_TABLE[isWhite ? r : 7 - r][c] + (1 - phase) * EG_P_TABLE[isWhite ? r : 7 - r][c];
                    case 'N', 'n' -> phase * MG_N_TABLE[isWhite ? r : 7 - r][c] + (1 - phase) * EG_N_TABLE[isWhite ? r : 7 - r][c];
                    case 'B', 'b' -> phase * MG_B_TABLE[isWhite ? r : 7 - r][c] + (1 - phase) * EG_B_TABLE[isWhite ? r : 7 - r][c];
                    case 'R', 'r' -> phase * MG_R_TABLE[isWhite ? r : 7 - r][c] + (1 - phase) * EG_R_TABLE[isWhite ? r : 7 - r][c];
                    case 'Q', 'q' -> phase * MG_Q_TABLE[isWhite ? r : 7 - r][c] + (1 - phase) * EG_Q_TABLE[isWhite ? r : 7 - r][c];
                    case 'K', 'k' -> phase * MG_K_TABLE[isWhite ? r : 7 - r][c] + (1 - phase) * EG_K_TABLE[isWhite ? r : 7 - r][c];
                    default -> 0;
                };
            }
        }
        return centipawns / 100;
    }

    /**
     * @return a double between 0 and 1, where a larger number means that the game is in the
     * early game phase while a smaller number means that the game is in the endgame phase.
     */
    private double getGamePhase(Board board) {
        double phase = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                phase += switch (board.getPieceAt(r, c)) {
                    case 'Q', 'q' -> Q_PHASE_WT;
                    case 'R', 'r' -> R_PHASE_WT;
                    case 'B', 'b' -> B_PHASE_WT;
                    case 'N', 'n' -> N_PHASE_WT;
                    default -> 0;
                };
            }
        }
        return Math.min(1.0, phase / TOTAL_WT);  // early promotions may set phase to be larger than TOTAL_WT
    }
}
