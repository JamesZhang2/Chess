package model.player;

import model.move.Move;

public record EvalMovePair(double eval, Move move) {
    @Override
    public String toString() {
        return move + ": " + eval;
    }
}
