package application;

/**
 * A class representing a response to a move received from the frontend.
 */
public class UIMoveResponse {
    public final boolean isLegal;
    public final String fen;
    public final String winner;  // u, w, b, or d

    public UIMoveResponse(boolean isLegal, String fen, char winner) {
        this.isLegal = isLegal;
        this.fen = fen;
        this.winner = String.valueOf(winner);
    }
}
