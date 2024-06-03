package model.player;

import model.board.Board;
import model.move.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An AI that makes random moves.
 */
public class RandomAIPlayer extends Player {

    public RandomAIPlayer(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public Action play(Board board) {
        List<Move> legalMoves = new ArrayList<>(board.getLegalMoves());
        Random random = new Random();
        int selection = random.nextInt(legalMoves.size());
        return new Action(legalMoves.get(selection));
    }

    @Override
    public boolean considerDraw(Board board) {
        Random random = new Random();
        return random.nextInt(2) == 0;
    }
}
