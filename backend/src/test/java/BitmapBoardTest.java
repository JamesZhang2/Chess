import model.*;
import org.junit.jupiter.api.Test;

class BitmapBoardTest extends BoardTest{
    @Override
    protected Board createBoard() {
        return new BitmapBoard();
    }

    @Override
    protected Board createBoard(String fen) throws IllegalBoardException, MalformedFENException {
        return new BitmapBoard(fen);
    }
}
