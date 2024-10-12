package model.player;

import model.move.Move;

import java.util.Objects;

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

    @Override
    public String toString() {
        if (actionType == Type.RESIGN) {
            return "Resign";
        } else if (actionType == Type.OFFER_DRAW) {
            return "Offer Draw";
        } else {
            return move.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Action)) {
            return false;
        }
        Action action = (Action) o;
        if (actionType != action.actionType) {
            return false;
        }
        if (actionType == Type.MOVE) {
            return move.equals(action.move);
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionType, move);
    }
}
