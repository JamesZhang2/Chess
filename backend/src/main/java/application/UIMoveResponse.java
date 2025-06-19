package application;

/**
 * A class representing a response to a move received from the frontend.
 */
public class UIMoveResponse {
    public final String fen;
    public final boolean isLegal;

    public UIMoveResponse(String fen, boolean isLegal) {
        this.fen = fen;
        this.isLegal = isLegal;
    }
}
