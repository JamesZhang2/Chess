package application;

public record InitGameRequest(String whiteName,
                              String whitePlayerType,
                              String blackName,
                              String blackPlayerType,
                              String handicapType) {
}
