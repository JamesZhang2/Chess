package application;

/**
 * A class representing a response to a move received from the frontend.
 */
public class UIMoveResponse {
    public final String fen;
    public final boolean isLegal;
    public final String winner;  // u, w, b, or d

    public UIMoveResponse(String fen, boolean isLegal, char winner) {
        this.fen = fen;
        this.isLegal = isLegal;
        this.winner = String.valueOf(winner);
    }
}
