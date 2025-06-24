package application;

/**
 * Represents a challenge created by a user.
 * This is both used to store the challenge and used to communicate between the frontend and the backend.
 */
public class Challenge {
    public enum Status {
        PENDING,  // challenge is waiting to be matched
        MATCHED,  // challenge is matched, but the frontend has not been notified to start a game
        RESOLVED,  // challenge is resolved - the frontend has started a game
        CANCELED  // challenge is canceled
    }

    public int challengeId;
    public int gameId;  // the gameId of the game related to this challenge if it's MATCHED or RESOLVED, or -1 if it's PENDING or CANCELED
    public Status status;  // "pending", "matched", "resolved", or "canceled"
    public final String username;
    public String side;  // side of the player who created the challenge
    public final String relHandicapType;  // relative handicap type (contains "SELF" or "OP") for the player who created the challenge
    public String opUsername;  // username of the opponent

    public Challenge(String username, String side, String relHandicapType) {
        this.challengeId = -1;
        this.gameId = -1;
        this.status = Status.PENDING;
        this.username = username;
        this.side = side;
        this.relHandicapType = relHandicapType;
    }

    public void cancel() {
        if (!status.equals(Status.PENDING)) {
            throw new IllegalStateException("Can only call cancel() on pending challenges");
        }
        status = Status.CANCELED;
    }

    public void match(String opUsername, int gameId, String newSide) {
        if (!status.equals(Status.PENDING)) {
            throw new IllegalStateException("Can only call match() on pending challenges");
        }
        if (newSide.equals("random")) {
            throw new IllegalArgumentException("newSide must be either white or black to avoid both players getting the same color");
        }
        if (this.side.equals("random")) {
            this.side = newSide;
        } else if (!this.side.equals(newSide)) {
            throw new IllegalArgumentException("newSide attempts to change a non-random previous side");
        }
        this.status = Status.MATCHED;
        this.opUsername = opUsername;
        this.gameId = gameId;
    }

    public void resolve() {
        if (!status.equals(Status.MATCHED)) {
            throw new IllegalStateException("Can only call resolve() on matched challenges");
        }
        status = Status.RESOLVED;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "challengeId=" + challengeId +
                ", status=" + status +
                ", username='" + username + '\'' +
                ", side='" + side + '\'' +
                ", relHandicapType='" + relHandicapType + '\'' +
                ", opUsername='" + opUsername + '\'' +
                '}';
    }
}
