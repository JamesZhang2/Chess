import java.util.List;
import java.util.Set;

public class Board {
    private Piece[][] pieces;
    private boolean whiteMove;
    private boolean whiteCastleK, whiteCastleQ, blackCastleK, blackCastleQ;
    private Set<List<Integer>> enPassant;
    private int halfMove;
    private int fullMove;

    /** Create board from FEN */
    public Board(String fen) {
        // TODO
    }
}
