package application;

public class InitGameRequest {
    public final String whitePlayerType;
    public final String blackPlayerType;
    public final String handicapType;

    public InitGameRequest(String whitePlayerType, String blackPlayerType, String handicapType) {
        this.whitePlayerType = whitePlayerType;
        this.blackPlayerType = blackPlayerType;
        this.handicapType = handicapType;
    }
}
