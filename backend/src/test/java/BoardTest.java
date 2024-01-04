import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {


    @Test
    public void TestFENParser() throws IllegalBoardException, MalformedFENException {
        Board start = new Board();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", start.toFEN());
        String[] validFens = {
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2",
                "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
                "r1bqk2r/ppp1bppp/2n5/3pP2n/2B5/5N2/PBQ2PPP/RN3RK1 w kq d6 0 10",
                "rnbqkbr1/pppppppp/5n2/8/4P3/2N5/PPPP1PPP/1RBQKBNR b Kq - 4 3",
                "rnbqkbnK/pppppppp/8/8/8/8/PPPPPPPP/RNBQrBNR w q - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 1 1",
                "rnbqkbnr/7p/p2P2p1/1pP1Pp2/1Pp1pP2/P2p2P1/7P/RNBQKBNR w KQkq - 1 10",
                "3k2BN/8/8/7K/8/8/8/8 w - - 90 55",
                "3k2BN/7P/8/7K/8/8/p7/8 b - - 90 55",
                "rnb1kbnr/pppp1ppp/4p3/8/5PPq/8/PPPPP2P/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/7P/8/PPPPPPP1/RNBQKBNR b KQkq h3 0 1",
                "1r3r2/4ppkp/1q2b1p1/p2pn3/2P5/1P1B3P/P2QNPP1/R3R1K1 w - - 0 20",
                "2r5/5R2/1R4pp/5pk1/1K6/P3PP1P/8/8 b - - 0 36",
                "rnb1kbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQK2q w Qkq - 0 1",
                "8/8/8/8/8/7k/7p/7K w - - 0 1",
                "RRRQQQQQ/8/8/8/7K/8/7k/8 w - - 0 1",
                "RRRQQQQQ/8/8/8/7K/8/5nnk/nnnnnnnn w - - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w kq - 0 1",
                "rnbqkbnr/2p2p2/2p2p2/1Pp1Pp2/1Pp1Pp2/1P2P3/1P2P3/RNBQKBNR w KQkq - 0 1",
                "rnbq1bnr/2p2pk1/2p2p2/1Pp1Pp2/1Pp1Pp2/1P2P3/1P1KP3/RNBQ1BNR w - - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 100 54",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 99 54",
                "rnbqkb1r/pppppppp/5n2/8/8/5N2/PPPPPPPP/RNBQKB1R w Qq - 6 4",
        };
        for (String validFen : validFens) {
            Board board = new Board(validFen);
            assertEquals(validFen, board.toFEN());
        }

        String[] malformedFens = {
                // Missing/Too many fields
                "",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 2",
                // Placement field error
                "rnbqkbnr/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/7/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/3b3/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/9/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnrr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PAPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPP./RNBQKBNR w KQkq - 0 1",
                // Invalid characters in other fields
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR white KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR B KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR 0 KQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KKQkq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQqq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w abcq - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w -q - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w 0 - 0 1",
                // Note that en passant target must be either 3 or 6
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e9 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e4 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e10 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq 3 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq d3f6 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - a 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -1 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0.5 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 a",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0",
        };
        for (String malformedFen : malformedFens) {
            assertThrows(MalformedFENException.class, () -> new Board(malformedFen));
        }

        // These FENs are not malformed, but they represent illegal positions
        // (number of white/black kings != 1; king in check when it's not one's turn,
        // pawns on the first or last rank)
        String[] illegalFens = {
                // Wrong number of kings
                "8/8/8/8/8/8/8/8 w - - 0 1",
                "8/8/8/8/8/8/8/4K3 w - - 1 1",
                "7k/8/8/8/8/8/8/8 w - - 0 1",
                "8/8/8/4k3/8/8/3K2K1/8 w - - 1 1",
                // King in check when it's not one's turn
                "8/8/8/4k3/3K4/8/8/8 w - - 1 1",
                "8/8/8/4k3/3K4/8/8/8 b - - 1 1",
                "R3k3/8/8/8/8/8/8/4K3 w - - 0 1",
                "4k3/8/8/8/8/8/3PPP2/4K2r b - - 0 1",
                "4k3/8/3N4/8/8/8/3PPP2/4K1N1 w - - 0 1",
                // Pawn on first or last rank
                "4k3/8/8/8/8/8/8/P3K3 w - - 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPP1/RNBQKBNP w Qkq - 0 1",
                "rnbqkbnP/pppppppp/8/8/8/8/PPPPPPP1/RNBQKBNR w Qq - 0 1",
                "rnbqkpnr/ppppp1pp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                "rnbqk1nr/ppppp1pp/8/8/8/8/PPPPPPPP/RNBQKpNR w KQkq - 0 1",
        };
        for (String illegalFen : illegalFens) {
            assertThrows(IllegalBoardException.class, () -> new Board(illegalFen));
        }
    }
}