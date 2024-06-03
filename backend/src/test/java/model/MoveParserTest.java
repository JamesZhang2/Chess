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
        // TODO
    }
    /*
     * Match:
     * e5+
     * Kb1+
     * Rb3+
     * Rxb2+
     * Qxf7#
     * axb3#
     *
     * Rab3
     * R3b3
     * Ra3b3
     * Qgxf7
     * Q7xf7
     * Qg7xf7
     *
     * O-O
     * O-O-O
     * O-O+
     * O-O-O#
     * 0-0
     * 0-0-0
     * 0-0#
     * 0-0-0#
     *
     * a8=Q
     * b8=R
     * c1=B+
     * h1=N#
     * axb8=Q
     * exd8=B+
     * cxd1=R+
     * gxh1=N#
     *
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