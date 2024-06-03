package model.player;

import model.move.Move;

/**
 * An action by a player: move, resign, or offer draw
 */
public class Action {
    public enum Type {
        MOVE,
        RESIGN,
        OFFER_DRAW
    }

    private final Type actionType;
    private Move move;

    public Action(Type actionType) {
        assert actionType == Type.RESIGN || actionType == Type.OFFER_DRAW;
        this.actionType = actionType;
    }

    public Action(Move move) {
        this.actionType = Type.MOVE;
        this.move = move;  // move is immutable so this should be fine
    }

    public Type getActionType() {
        return actionType;
    }

    public Move getMove() {
        assert actionType == Type.MOVE;
        return move;
    }
}
