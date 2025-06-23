package model.player;

import model.board.Board;

public class HumanGUIPlayer extends Player {
    public HumanGUIPlayer(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public Action play(Board board) {
        // This should not be called. All Human GUI player actions should be sent from the frontend.
        assert false;
        throw new UnsupportedOperationException("All Human GUI player actions should be sent from the frontend");
    }

    @Override
    public boolean considerDraw(Board board) {
        // this should not be called. All Human GUI player actions should be sent from the frontend.
        assert false;
        throw new UnsupportedOperationException("All Human GUI player actions should be sent from the frontend");
    }
}
