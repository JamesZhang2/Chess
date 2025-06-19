package model.player;

import model.board.Board;

public class HumanGUIPlayer extends Player {
    public HumanGUIPlayer(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public Action play(Board board) {
        return null;
    }

    @Override
    public boolean considerDraw(Board board) {
        return false;
    }
}
