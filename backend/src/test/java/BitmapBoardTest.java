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

    @Test
    void printCastlingBitmaps() {
        Util.printBitmap(0x60L);
        Util.printBitmap(0x70L);
        Util.printBitmap(0xEL);
        Util.printBitmap(0x1CL);
        Util.printBitmap(0x60L << 56);
        Util.printBitmap(0x70L << 56);
        Util.printBitmap(0xEL << 56);
        Util.printBitmap(0x1CL << 56);
    }
}
