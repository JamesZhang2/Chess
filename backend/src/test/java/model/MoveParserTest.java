package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveParserTest {

    private void assertParseEquals(Move move, String input, Board board)
            throws MalformedMoveException, IllegalMoveException, AmbiguousMoveException {
        assertEquals(move, MoveParser.parse(input, board));
    }

    @Test
    void testParse() throws MalformedMoveException, IllegalMoveException, AmbiguousMoveException,
            IllegalBoardException, MalformedFENException {
        // Board representation should not matter
        // Starting position
        assertParseEquals(Util.moveFromSquares("e2", "e4", false, false),
                "e4", new BitmapBoard());
        assertParseEquals(Util.moveFromSquares("h2", "h3", false, false),
                "h3", new BitmapBoard());
        assertParseEquals(Util.moveFromSquares("g1", "f3", false, false),
                "Nf3", new BitmapBoard());
        // Quiet moves
        assertParseEquals(Util.moveFromSquares("h1", "h7", false, false),
                "Qh7", new BitmapBoard("4k3/8/8/8/8/8/8/4K2Q w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("b4", "d6", false, false),
                "Qd6", new BitmapBoard("4k3/8/8/8/1Q6/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("e1", "e2", false, false),
                "Ke2", new BitmapBoard("4k3/8/8/8/1Q6/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("d3", "f3", false, false),
                "Rf3", new BitmapBoard("4k3/8/8/8/8/3R4/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("e6", "d5", false, false),
                "Bd5", new BitmapBoard("4k3/8/2pnb3/8/8/8/8/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("d6", "b5", false, false),
                "Nb5", new BitmapBoard("4k3/8/2pnb3/8/8/8/8/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("c6", "c5", false, false),
                "c5", new BitmapBoard("4k3/8/2pnb3/8/8/8/8/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h8", "a1", false, false),
                "Ba1", new BitmapBoard("4k2b/p7/8/8/8/8/8/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("a1", "h8", false, false),
                "Bh8", new BitmapBoard("4k3/p7/8/8/8/8/8/B3K3 w - - 0 1"));
        // Captures
        assertParseEquals(Util.moveFromSquares("h5", "g6", false, true),
                "hxg6", new MailboxBoard("4k3/8/6q1/7P/8/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("a2", "a4", false, true),
                "Qxa4", new MailboxBoard("8/7k/5r2/8/n7/2B5/Q4P2/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("a4", "c3", false, true),
                "Nxc3", new MailboxBoard("8/7k/5r2/8/n7/2B5/Q4P2/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("c3", "f6", false, true),
                "Bxf6", new MailboxBoard("8/7k/5r2/8/n7/2B5/Q4P2/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("f6", "f2", false, true),
                "Rxf2", new MailboxBoard("8/7k/5r2/8/n7/2B5/Q4P2/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("e1", "f2", false, true),
                "Kxf2", new MailboxBoard("8/7k/8/8/n7/2B5/Q4r2/4K3 w - - 0 1"));
        // Castling
        assertParseEquals(new Move('K'),
                "O-O", new MailboxBoard("r3k2r/p6p/8/8/8/8/P6P/R3K2R w KQkq - 0 1"));
        assertParseEquals(new Move('Q'),
                "O-O-O", new MailboxBoard("r3k2r/p6p/8/8/8/8/P6P/R3K2R w Q - 0 1"));
        assertParseEquals(new Move('k'),
                "0-0", new MailboxBoard("r3k2r/p6p/8/8/8/8/P6P/R3K2R b k - 0 1"));
        assertParseEquals(new Move('q'),
                "0-0-0", new MailboxBoard("r3k2r/p6p/8/8/8/8/P6P/R3K2R b q - 0 1"));
        // Checks and Checkmates
        assertParseEquals(Util.moveFromSquares("h7", "g6", false, false),
                "Qg6+", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h7", "f5", false, true),
                "Qxf5+", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("b8", "b6", false, false),
                "Rb6+", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("e1", "d2", false, false),
                "Bd2+", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h2", "f1", false, false),
                "Nf1+", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("f5", "f4", false, false),
                "f4+", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("e6", "d6", false, false),
                "Kd6+", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h7", "h8", false, false),
                "Rh8#", new MailboxBoard("4k3/R6R/8/8/8/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h5", "f7", false, true),
                "Qxf7#", new MailboxBoard("3qk3/5p2/8/7Q/2B5/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("c4", "f7", false, true),
                "Bxf7+", new MailboxBoard("3qk3/5p2/8/7Q/2B5/8/8/4K3 w - - 0 1"));
        assertParseEquals(new Move('K'),
                "O-O#", new MailboxBoard("rnbqrkrb/ppppp1pp/8/8/8/8/PPPPP1PP/RNBQK2R w KQ - 0 1"));
        assertParseEquals(new Move('q'),
                "0-0-0+", new MailboxBoard("r3k3/ppp1p3/8/3K4/8/8/PPPPP1PP/R7 b q - 0 1"));
        assertParseEquals(Util.moveFromSquares("e7", "f6", false, true),
                "exf6+", new MailboxBoard("r3k3/ppp1p3/5Q2/6K1/8/8/PPPPP1PP/R7 b q - 0 1"));
        assertParseEquals(Util.moveFromSquares("g6", "f7", false, true),
                "gxf7#", new MailboxBoard("r3k3/ppp2pK1/6P1/8/3Q3B/8/PPPPP1PP/R7 w - - 0 1"));
        // Checks and mates don't have to be announced
        assertParseEquals(Util.moveFromSquares("e6", "d6", false, false),
                "Kd6", new MailboxBoard("1R6/4r2Q/4k3/5p2/8/4K3/7n/4b3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h7", "h8", false, false),
                "Rh8", new MailboxBoard("4k3/R6R/8/8/8/8/8/4K3 w - - 0 1"));
        // En passant
        assertParseEquals(Util.moveFromSquares("g5", "f6", true, true),
                "gxf6", new BitmapBoard("4k3/8/8/5pP1/8/8/8/4K3 w - f6 0 2"));
        assertParseEquals(Util.moveFromSquares("c4", "d3", true, true),
                "cxd3", new BitmapBoard("4k3/8/8/8/2pP4/8/8/4K3 b - d3 0 1"));
        assertParseEquals(Util.moveFromSquares("f4", "e3", true, true),
                "fxe3+", new BitmapBoard("rnbqkbn1/ppppprpp/8/8/4Pp2/5K2/PPPP1PPP/RNBQ1BNR b q e3 0 1"));
        assertParseEquals(Util.moveFromSquares("e5", "d6", true, true),
                "exd6#", new BitmapBoard("rn1qqqnr/4bkpp/8/3pP1BP/2B5/8/PPPPPP2/4K3 w - d6 0 2"));
        // Promotions
        assertParseEquals(Util.moveFromSquares("h7", "h8", 'Q', false),
                "h8=Q", new BitmapBoard("8/4k2P/8/8/8/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h7", "h8", 'R', false),
                "h8=R", new BitmapBoard("8/4k2P/8/8/8/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h7", "h8", 'B', false),
                "h8=B", new BitmapBoard("8/4k2P/8/8/8/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h7", "h8", 'N', false),
                "h8=N", new BitmapBoard("8/4k2P/8/8/8/8/8/4K3 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("c2", "c1", 'q', false),
                "c1=Q+", new BitmapBoard("4k3/8/8/8/8/8/2p5/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("c2", "c1", 'r', false),
                "c1=R+", new BitmapBoard("4k3/8/8/8/8/8/2p5/4K3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("c2", "d1", 'b', true),
                "cxd1=B", new BitmapBoard("4k3/8/8/8/8/8/2p5/3QK3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("c2", "d1", 'r', true),
                "cxd1=R+", new BitmapBoard("4k3/8/8/8/8/8/2p5/3QK3 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("e2", "f1", 'n', true),
                "exf1=N#", new BitmapBoard("4k3/8/8/8/8/7Q/4p1PK/5QBQ b - - 0 2"));
        // Disambiguation (can be used even if there is no ambiguity)
        assertParseEquals(Util.moveFromSquares("g1", "f3", false, false),
                "Ng1f3", new BitmapBoard());
        assertParseEquals(Util.moveFromSquares("g1", "f3", false, false),
                "Ngf3", new BitmapBoard());
        assertParseEquals(Util.moveFromSquares("g1", "f3", false, false),
                "N1f3", new BitmapBoard());
        assertParseEquals(Util.moveFromSquares("e8", "f8", false, false),
                "Ref8", new BitmapBoard("k3r1r1/1b1b4/5Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("g8", "f8", false, false),
                "Rgf8", new BitmapBoard("k3r1r1/1b1b4/5Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("e8", "f8", false, false),
                "Re8f8", new BitmapBoard("k3r1r1/1b1b4/5Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("a5", "a4", false, true),
                "R5xa4", new BitmapBoard("k3r1r1/1b1b4/5Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("a3", "a4", false, true),
                "R3xa4", new BitmapBoard("k3r1r1/1b1b4/5Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 1"));
        assertParseEquals(Util.moveFromSquares("h4", "h6", false, false),
                "Qh4h6", new BitmapBoard("k3r1r1/1b1b4/2P2Q2/rb1b4/Q4Q1Q/r7/8/3K4 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("f4", "h6", false, false),
                "Qf4h6", new BitmapBoard("k3r1r1/1b1b4/2P2Q2/rb1b4/Q4Q1Q/r7/8/3K4 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("f6", "h6", false, false),
                "Qf6h6", new BitmapBoard("k3r1r1/1b1b4/2P2Q2/rb1b4/Q4Q1Q/r7/8/3K4 w - - 0 1"));
        assertParseEquals(Util.moveFromSquares("b7", "c6", false, true),
                "Bb7xc6", new BitmapBoard("k3r1r1/1b1b4/2P2Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 2"));
        assertParseEquals(Util.moveFromSquares("d7", "c6", false, true),
                "Bd7xc6", new BitmapBoard("k3r1r1/1b1b4/2P2Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 2"));
        assertParseEquals(Util.moveFromSquares("b5", "c6", false, true),
                "Bb5xc6", new BitmapBoard("k3r1r1/1b1b4/2P2Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 2"));
        assertParseEquals(Util.moveFromSquares("d5", "c6", false, true),
                "Bd5xc6", new BitmapBoard("k3r1r1/1b1b4/2P2Q2/rb1b4/Q4Q1Q/r7/8/3K4 b - - 0 2"));
    }
    /*
     * Don't match:
     * a9
     * i2
     * Qxa
     * g11
     * xa
     * axb
     * axc+
     * O-0
     * O-O-
     * aa1
     */
}