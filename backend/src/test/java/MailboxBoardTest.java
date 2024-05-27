import model.*;
import org.junit.jupiter.api.Test;

class MailboxBoardTest extends BoardTest{
    @Override
    protected Board createBoard() {
        return new MailboxBoard();
    }

    @Override
    protected Board createBoard(String fen) throws IllegalBoardException, MalformedFENException {
        return new MailboxBoard(fen);
    }
}
