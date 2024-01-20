import model.Board;
import model.IllegalBoardException;
import model.MalformedFENException;
import model.Move;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private final boolean printMoves = false;

    @Test
    public void testFENParser() throws IllegalBoardException, MalformedFENException {
        Board start = new Board();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", start.toFEN());
        String[] validFENs = {
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
        for (String validFEN : validFENs) {
            Board board = new Board(validFEN);
            assertEquals(validFEN, board.toFEN());
        }

        String[] malformedFENs = {
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
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq j3 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq A6 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e4 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e10 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq . 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq 3 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq d3f6 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - a 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -1 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0.5 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 a",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0.5",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0",
                // En passant field does not match board state
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1",
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq e6 0 1",
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 1",
                "rnbqkbnr/pppp1ppp/8/4p3/8/8/PPPPPPPP/RNBQKBNR b KQkq e6 0 1",
        };
        for (String malformedFEN : malformedFENs) {
            assertThrows(MalformedFENException.class, () -> new Board(malformedFEN), malformedFEN);
        }

        // These FENs are not malformed, but they represent illegal positions
        // (number of white/black kings != 1; king in check when it's not one's turn,
        // pawns on the first or last rank)
        String[] illegalFENs = {
                // Wrong number of kings
                "8/8/8/8/8/8/8/8 w - - 0 1",
                "8/8/8/8/8/8/8/4K3 w - - 1 1",
                "7k/8/8/8/8/8/8/8 w - - 0 1",
                "8/8/8/4k3/8/8/3K2K1/8 w - - 1 1",
                // King in check when it's not one's turn
                "8/8/8/4k3/3K4/8/8/8 w - - 1 1",
                "8/8/8/4k3/3K4/8/8/8 b - - 1 1",
                "8/8/8/8/8/1k6/Pp6/K7 w - - 0 1",
                "8/8/8/8/8/1k6/Pp6/K7 b - - 0 1",
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
        for (String illegalFEN : illegalFENs) {
            assertThrows(IllegalBoardException.class, () -> new Board(illegalFEN), illegalFEN);
        }
    }

    @Test
    public void testInCheck() throws IllegalBoardException, MalformedFENException {
        // Also tests the private model.Board.controls method
        String[] inCheckFENs = {
                // Queen
                "4k3/8/8/8/4K1q1/8/8/8 w - - 0 1",
                "4k3/4q3/8/8/4K3/8/8/8 w - - 0 1",
                "4k3/8/8/8/3qK3/8/8/8 w - - 0 1",
                "4k3/8/8/8/4K3/8/8/4q3 w - - 0 1",
                "4k3/1q6/8/8/4K3/8/8/8 w - - 0 1",
                "4k3/8/8/5q2/4K3/8/8/8 w - - 0 1",
                "4k3/8/8/8/4K3/8/6q1/8 w - - 0 1",
                "4k3/8/8/8/4K3/8/8/1q6 w - - 0 1",
                // Rook
                "4k3/8/8/8/8/8/8/r3K3 w - - 0 1",
                "4k3/8/8/8/8/8/8/3rK3 w - - 0 1",
                "4k3/8/8/8/8/8/4r3/4K3 w - - 0 1",
                "4k3/8/8/8/8/8/8/4K1r1 w - - 0 1",
                "4k3/8/8/8/4K3/4r3/8/8 w - - 0 1",
                "7k/8/8/8/8/7P/6P1/2r4K w - - 0 1",
                "8/8/4k3/4r3/8/4K3/4R3/8 w - - 0 1",
                "4K3/8/8/8/8/1k2r3/4p3/4q3 w - - 0 1",
                // Bishop
                "7k/8/8/3b4/8/5K2/8/8 w - - 0 1",
                "7k/8/8/6b1/8/4K3/8/8 w - - 0 1",
                "7k/K7/8/8/8/8/8/6b1 w - - 0 1",
                "7k/8/8/8/8/8/2K5/1b6 w - - 0 1",
                "7k/8/8/8/b7/3P4/2K5/8 w - - 0 1",
                // Knight
                "7k/5n2/8/4K3/8/8/8/8 w - - 0 1",
                "7k/3n4/8/4K3/8/8/8/8 w - - 0 1",
                "7k/8/8/1n6/3K4/8/8/8 w - - 0 1",
                "7k/8/8/8/3K4/1n6/8/8 w - - 0 1",
                "7k/8/8/8/3K4/8/2n5/8 w - - 0 1",
                "7k/8/8/8/3K4/8/4n3/8 w - - 0 1",
                "8/7k/8/8/3K4/5n2/8/8 w - - 0 1",
                "8/7k/8/8/8/8/2n5/K7 w - - 0 1",
                "4k3/4n3/3PP3/3K4/8/8/8/8 w - - 0 1",
                "4k3/8/3PP3/3K4/1np5/8/8/8 w - - 0 1",
                "4k3/8/8/8/8/6K1/6Br/6Qn w - - 0 1",
                "4k1Bn/3b2PP/6K1/8/8/8/8/8 w - - 0 1",
                // Pawn
                "4k3/8/8/5p2/4K3/8/8/8 w - - 0 1",
                "4k3/8/8/8/p7/1K6/8/8 w - - 0 1",
                "8/8/8/8/8/7k/7p/6K1 w - - 0 1",
                "8/8/8/8/8/1k6/1p1P4/K7 w - - 0 1",
                // Black in check
                "8/8/6K1/5B2/8/2kQ4/8/8 b - - 0 1",
                "4k1R1/8/8/8/8/8/4K3/8 b - - 0 1",
                "8/8/4B1K1/4B3/8/2k5/8/8 b - - 0 1",
                "4k3/6N1/8/8/8/8/4K3/8 b - - 0 1",
                "8/8/8/3k4/2P5/1K6/8/8 b - - 0 1",
                "8/8/8/6k1/7P/1K6/8/8 b - - 0 1",
                // White is in check even though the black knight is pinned
                "8/8/8/8/3kn1R1/8/5K2/8 w - - 0 1",
                // Mated
                "4k3/8/8/8/8/8/7r/r2K4 w - - 0 1",
                "7k/8/5BKN/8/8/8/8/8 b - - 0 1",
                "2nkb3/2ppp3/4N3/8/8/3Q4/3K4/8 b - - 0 1",
        };
        for (String fen : inCheckFENs) {
            Board board = new Board(fen);
            assert board.isInCheck();
        }

        String[] notInCheckFENs = {
                // Starting position
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                // Two kings
                "k7/8/8/8/8/8/8/7K w - - 0 1",
                // Queen
                "8/8/4k3/4q3/4P3/4K3/8/8 w - - 0 1",
                "8/8/4k3/5q2/4P3/4K3/8/8 w - - 0 1",
                "8/8/4k3/8/4P3/4K3/4P3/4q3 w - - 0 1",
                "8/8/4k3/8/4P3/4KP1q/8/8 w - - 0 1",
                "8/8/4k2q/6P1/8/4K3/8/8 w - - 0 1",
                "4K3/8/4k3/8/8/8/8/4q3 w - - 0 1",
                "4K3/8/8/8/8/1k2b3/8/4q3 w - - 0 1",
                // Rook
                "8/4K1pr/8/8/8/1k6/8/8 w - - 0 1",
                "8/4K1Pr/8/8/8/1k6/8/8 w - - 0 1",
                "6r1/6P1/6K1/8/8/1k6/8/8 w - - 0 1",
                // Bishop
                "7b/6P1/5K2/8/8/1k6/8/8 w - - 0 1",
                "8/8/8/b7/1n6/1k6/3K4/8 w - - 0 1",
                "8/8/2K5/8/4r3/1k3r2/6b1/8 w - - 0 1",
                "8/8/2K5/8/4N3/1k3r2/6b1/8 w - - 0 1",
                "8/3K4/4N3/8/8/1k5r/6b1/8 w - - 0 1",
                "8/3K4/4N3/8/3b4/1k6/8/8 w - - 0 1",
                // Knight
                "8/4n3/8/6K1/8/1k6/8/8 w - - 0 1",
                "8/4n1n1/8/4n1K1/8/1k2n1n1/8/8 w - - 0 1",
                // Pawn
                "k7/8/6p1/6K1/8/8/8/8 w - - 0 1",
                "k7/8/8/6K1/8/8/6p1/8 w - - 0 1",
                // Other positions
                "r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 0 1",
                "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/2N2N2/PPPP1PPP/R1BQ1RK1 w kq - 0 1",
                "7k/7P/7K/8/8/8/8/8 b - - 0 1",
                "7k/7B/7K/8/8/8/8/8 b - - 0 1",
                "2nkb3/2ppp3/8/8/8/2RQR3/3K4/8 b - - 0 1",
                "8/8/8/8/7p/3k2pP/5pP1/5K1B w - - 12 7",
        };
        for (String fen : notInCheckFENs) {
            Board board = new Board(fen);
            assert !board.isInCheck();
        }
    }

    /**
     * Asserts that the move is legal and the resulting FEN after making the move is the same as expectedFEN.
     * Also tests that undoing the last move gets us back to the original position.
     */
    private void testLegalMove(String fen, Move move, String expectedFEN)
            throws IllegalBoardException, MalformedFENException {
        Board board = new Board(fen);
        boolean legal = board.move(move);
        String msg = "Original FEN: " + fen + ", move: " + move;
        assert legal : "Move should be legal but judged as illegal. " + msg;
        assertEquals(expectedFEN, board.toFEN(), msg);

        boolean canUndo = board.undoLastMove();
        assert canUndo : "It should be possible to undo the previous move, " +
                "but the program thinks it is impossible. " + msg;
        assertEquals(fen, board.toFEN(), msg);
    }

    /**
     * Asserts that the move is illegal and the FEN of the board after attempting the move is unchanged.
     * Also tests that undoing the last move is impossible and calling undoLastMove leaves the board unchanged
     * since the board was just created and no legal moves were made.
     */
    private void testIllegalMove(String fen, Move move)
            throws IllegalBoardException, MalformedFENException {
        Board board = new Board(fen);
        boolean legal = board.move(move);
        String msg = "Original FEN: " + fen + ", move: " + move;
        assert !legal : "Move should be illegal but judged as legal. " + msg;
        assertEquals(fen, board.toFEN(), msg);

        boolean canUndo = board.undoLastMove();
        assertFalse(canUndo, msg);
        assertEquals(fen, board.toFEN(), msg);
    }

    /**
     * Turns a square (like f3) into coordinates in {row, col} form (like [2, 5]).
     */
    private int[] squareToCoords(String square) {
        int row = square.charAt(1) - '1';
        int col = square.charAt(0) - 'a';
        return new int[]{row, col};
    }

    /**
     * Convenience method for constructing moves
     * @return the move constructed from the given fields
     */
    private Move moveFromSquares(String from, String to, boolean isEnPassant, boolean isCapture) {
        int[] startCoord = squareToCoords(from);
        int[] endCoord = squareToCoords(to);
        return new Move(startCoord[0], startCoord[1], endCoord[0], endCoord[1], isEnPassant, isCapture);
    }

    private Move moveFromSquares(String from, String to, char promotion, boolean isCapture) {
        int[] startCoord = squareToCoords(from);
        int[] endCoord = squareToCoords(to);
        return new Move(startCoord[0], startCoord[1], endCoord[0], endCoord[1], promotion, isCapture);
    }

    @Test
    public void testMove() throws MalformedFENException, IllegalBoardException {
        // Legal moves
        // Stafford Gambit line
        testLegalMove("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                moveFromSquares("e2", "e4", false, false),
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        testLegalMove("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                moveFromSquares("e7", "e5", false, false),
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2");
        testLegalMove("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2",
                moveFromSquares("g1", "f3", false, false),
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        testLegalMove("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
                moveFromSquares("g8", "f6", false, false),
                "rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
        testLegalMove("rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3",
                moveFromSquares("f3", "e5", false, true),
                "rnbqkb1r/pppp1ppp/5n2/4N3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 0 3");
        testLegalMove("rnbqkb1r/pppp1ppp/5n2/4N3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 0 3",
                moveFromSquares("b8", "c6", false, false),
                "r1bqkb1r/pppp1ppp/2n2n2/4N3/4P3/8/PPPP1PPP/RNBQKB1R w KQkq - 1 4");
        testLegalMove("r1bqkb1r/pppp1ppp/2n2n2/4N3/4P3/8/PPPP1PPP/RNBQKB1R w KQkq - 1 4",
                moveFromSquares("e5", "c6", false, true),
                "r1bqkb1r/pppp1ppp/2N2n2/8/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 0 4");
        testLegalMove("r1bqkb1r/pppp1ppp/2N2n2/8/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 0 4",
                moveFromSquares("d7", "c6", false, true),
                "r1bqkb1r/ppp2ppp/2p2n2/8/4P3/8/PPPP1PPP/RNBQKB1R w KQkq - 0 5");
        // A few moves later (white has fallen into the trap)
        testLegalMove("r1bqk2r/ppp2ppp/2p5/2b5/2B1P1n1/2N5/PPPP1PPP/R1BQK2R w KQkq - 4 7",
                new Move('K'),
                "r1bqk2r/ppp2ppp/2p5/2b5/2B1P1n1/2N5/PPPP1PPP/R1BQ1RK1 b kq - 5 7");
        testLegalMove("r1bqk2r/ppp2ppp/2p5/2b5/2B1P1n1/2N5/PPPP1PPP/R1BQ1RK1 b kq - 5 7",
                moveFromSquares("d8", "h4", false, false),
                "r1b1k2r/ppp2ppp/2p5/2b5/2B1P1nq/2N5/PPPP1PPP/R1BQ1RK1 w kq - 6 8");

        // Queen moves
        testLegalMove("4k3/8/8/8/8/6Q1/8/4K3 w - - 0 1",
                moveFromSquares("g3", "d6", false, false),
                "4k3/8/3Q4/8/8/8/8/4K3 b - - 1 1");
        testLegalMove("8/3k4/8/3N1q2/8/2K5/8/8 b - - 15 20",
                moveFromSquares("f5", "d5", false, true),
                "8/3k4/8/3q4/8/2K5/8/8 w - - 0 21");
        testLegalMove("4k3/8/8/q7/6Q1/8/8/4K3 w - - 0 1",
                moveFromSquares("g4", "b4", false, false),
                "4k3/8/8/q7/1Q6/8/8/4K3 b - - 1 1");

        // Rook moves
        testLegalMove("4k3/8/8/8/6R1/8/8/4K3 w - - 0 21",
                moveFromSquares("g4", "g8", false, false),
                "4k1R1/8/8/8/8/8/8/4K3 b - - 1 21");
        testLegalMove("4k3/8/8/2r3P1/8/8/8/4K3 b - - 1 21",
                moveFromSquares("c5", "g5", false, true),
                "4k3/8/8/6r1/8/8/8/4K3 w - - 0 22");
        testLegalMove("4k3/8/4Q1r1/8/8/8/8/4K3 b - - 1 1",
                moveFromSquares("g6", "e6", false, true),
                "4k3/8/4r3/8/8/8/8/4K3 w - - 0 2");
        // Moving rook removes castling rights
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 22",
                moveFromSquares("a1", "b1", false, false),
                "r3k2r/8/8/8/8/8/8/1R2K2R b Kkq - 1 22");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 22",
                moveFromSquares("h1", "g1", false, false),
                "r3k2r/8/8/8/8/8/8/R3K1R1 b Qkq - 1 22");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 7 22",
                moveFromSquares("a8", "a2", false, false),
                "4k2r/8/8/8/8/8/r7/R3K2R w KQk - 8 23");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 7 22",
                moveFromSquares("h8", "f8", false, false),
                "r3kr2/8/8/8/8/8/8/R3K2R w KQq - 8 23");
        // Capturing rook removes castling rights
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 22",
                moveFromSquares("a1", "a8", false, true),
                "R3k2r/8/8/8/8/8/8/4K2R b Kk - 0 22");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 22",
                moveFromSquares("h1", "h8", false, true),
                "r3k2R/8/8/8/8/8/8/R3K3 b Qq - 0 22");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 7 22",
                moveFromSquares("a8", "a1", false, true),
                "4k2r/8/8/8/8/8/8/r3K2R w Kk - 0 23");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 7 22",
                moveFromSquares("h8", "h1", false, true),
                "r3k3/8/8/8/8/8/8/R3K2r w Qq - 0 23");
        // Moving a rook back does not give back castling rights
        testLegalMove("r3k2r/p6p/8/8/8/8/P6P/R3K1R1 w Qkq - 7 22",
                moveFromSquares("g1", "h1", false, false),
                "r3k2r/p6p/8/8/8/8/P6P/R3K2R b Qkq - 8 22");

        // Bishop moves
        testLegalMove("4k2q/8/8/8/8/8/1B6/4K3 w - - 0 1",
                moveFromSquares("b2", "h8", false, true),
                "4k2B/8/8/8/8/8/8/4K3 b - - 0 1");
        testLegalMove("8/8/7k/6b1/8/8/8/2QK4 b - - 0 1",
                moveFromSquares("g5", "f4", false, false),
                "8/8/7k/8/5b2/8/8/2QK4 w - - 1 2");

        // Knight moves
        testLegalMove("4k3/8/5p2/8/6N1/8/8/4K3 w - - 0 1",
                moveFromSquares("g4", "f6", false, true),
                "4k3/8/5N2/8/8/8/8/4K3 b - - 0 1");
        testLegalMove("4k3/8/6pp/6pn/6pp/8/8/4K3 b - - 0 1",
                moveFromSquares("h5", "f4", false, false),
                "4k3/8/6pp/6p1/5npp/8/8/4K3 w - - 1 2");

        // King moves
        testLegalMove("8/8/2k5/8/7P/5K2/8/8 w - - 0 1",
                moveFromSquares("f3", "e4", false, false),
                "8/8/2k5/8/4K2P/8/8/8 b - - 1 1");
        testLegalMove("8/8/2k5/8/4K2P/8/8/8 b - - 1 1",
                moveFromSquares("c6", "d7", false, false),
                "8/3k4/8/8/4K2P/8/8/8 w - - 2 2");
        // Moving out of check
        testLegalMove("8/8/8/4k3/8/4K2r/8/8 w - - 0 1",
                moveFromSquares("e3", "f2", false, false),
                "8/8/8/4k3/8/7r/5K2/8 b - - 1 1");
        testLegalMove("8/8/8/4kQ2/8/8/5K2/8 b - - 1 1",
                moveFromSquares("e5", "f5", false, true),
                "8/8/8/5k2/8/8/5K2/8 w - - 0 2");
        // Castling
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1",
                new Move('K'),
                "r3k2r/8/8/8/8/8/8/R4RK1 b kq - 1 1");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1",
                new Move('Q'),
                "r3k2r/8/8/8/8/8/8/2KR3R b kq - 1 1");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 1 1",
                new Move('k'),
                "r4rk1/8/8/8/8/8/8/R3K2R w KQ - 2 2");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 1 1",
                new Move('q'),
                "2kr3r/8/8/8/8/8/8/R3K2R w KQ - 2 2");
        // Moving king removes castling rights
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1",
                moveFromSquares("e1", "e2", false, false),
                "r3k2r/8/8/8/8/8/4K3/R6R b kq - 1 1");
        testLegalMove("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 1 1",
                moveFromSquares("e8", "e7", false, false),
                "r6r/4k3/8/8/8/8/8/R3K2R w KQ - 2 2");

        // Pawn moves
        // Pawn move should reset halfmove counter
        testLegalMove("8/8/8/4k3/2P5/4K3/8/8 w - - 49 40",
                moveFromSquares("c4", "c5", false, false),
                "8/8/8/2P1k3/8/4K3/8/8 b - - 0 40");
        testLegalMove("8/8/8/4k3/8/4K1p1/8/8 b - - 0 1",
                moveFromSquares("g3", "g2", false, false),
                "8/8/8/4k3/8/4K3/6p1/8 w - - 0 2");
        // Pawn captures
        testLegalMove("8/7b/6P1/4k3/8/4K3/8/8 w - - 0 2",
                moveFromSquares("g6", "h7", false, true),
                "8/7P/8/4k3/8/4K3/8/8 b - - 0 2");
        testLegalMove("8/8/8/4k3/8/4K1p1/5Q2/8 b - - 49 40",
                moveFromSquares("g3", "f2", false, true),
                "8/8/8/4k3/8/4K3/5p2/8 w - - 0 41");
        // Moving two squares from starting square changes en passant state
        testLegalMove("8/8/8/4k3/2p5/4K3/1P6/8 w - - 49 41",
                moveFromSquares("b2", "b4", false, false),
                "8/8/8/4k3/1Pp5/4K3/8/8 b - b3 0 41");
        testLegalMove("4k3/p7/8/1P6/8/8/8/4K3 b - - 0 1",
                moveFromSquares("a7", "a5", false, false),
                "4k3/8/8/pP6/8/8/8/4K3 w - a6 0 2");
        // Promotions
        testLegalMove("4k3/P7/8/8/8/8/8/4K3 w - - 49 2",
                moveFromSquares("a7", "a8", 'Q', false),
                "Q3k3/8/8/8/8/8/8/4K3 b - - 0 2");
        testLegalMove("4k3/P7/8/8/8/8/8/4K3 w - - 49 2",
                moveFromSquares("a7", "a8", 'R', false),
                "R3k3/8/8/8/8/8/8/4K3 b - - 0 2");
        testLegalMove("4k3/P7/8/8/8/8/8/4K3 w - - 49 2",
                moveFromSquares("a7", "a8", 'B', false),
                "B3k3/8/8/8/8/8/8/4K3 b - - 0 2");
        testLegalMove("4k3/P7/8/8/8/8/8/4K3 w - - 49 2",
                moveFromSquares("a7", "a8", 'N', false),
                "N3k3/8/8/8/8/8/8/4K3 b - - 0 2");
        testLegalMove("4k3/8/8/8/8/8/2p5/4K3 b - - 49 2",
                moveFromSquares("c2", "c1", 'q', false),
                "4k3/8/8/8/8/8/8/2q1K3 w - - 0 3");
        testLegalMove("4k3/8/8/8/8/8/2p5/4K3 b - - 49 2",
                moveFromSquares("c2", "c1", 'r', false),
                "4k3/8/8/8/8/8/8/2r1K3 w - - 0 3");
        testLegalMove("4k3/8/8/8/8/8/2p5/4K3 b - - 49 2",
                moveFromSquares("c2", "c1", 'b', false),
                "4k3/8/8/8/8/8/8/2b1K3 w - - 0 3");
        testLegalMove("4k3/8/8/8/8/8/2p5/4K3 b - - 49 2",
                moveFromSquares("c2", "c1", 'n', false),
                "4k3/8/8/8/8/8/8/2n1K3 w - - 0 3");
        testLegalMove("3qk3/2P5/8/8/8/8/8/4K3 w - - 0 3",
                 moveFromSquares("c7", "d8", 'N', true),
                "3Nk3/8/8/8/8/8/8/4K3 b - - 0 3");
        testLegalMove("4k3/8/8/8/8/8/1p6/B3K3 b - - 0 3",
                moveFromSquares("b2", "a1", 'r', true),
                "4k3/8/8/8/8/8/8/r3K3 w - - 0 4");
        // En passant
        testLegalMove("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 4",
                moveFromSquares("e5", "f6", true, true),
                "rnbqkbnr/ppp1p1pp/5P2/3p4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 4");
        testLegalMove("rnbqkbnr/ppp1pppp/8/8/2PpPP2/8/PP1P2PP/RNBQKBNR b KQkq c3 0 6",
                moveFromSquares("d4", "c3", true, true),
                "rnbqkbnr/ppp1pppp/8/8/4PP2/2p5/PP1P2PP/RNBQKBNR w KQkq - 0 7");
        testLegalMove("8/8/8/1k1pP3/4K3/8/8/8 w - d6 0 2",
                moveFromSquares("e5", "d6", true, true),
                "8/8/3P4/1k6/4K3/8/8/8 b - - 0 2");
        testLegalMove("8/8/8/6k1/5Pp1/4K3/8/8 b - f3 0 1",
                moveFromSquares("g4", "f3", true, true),
                "8/8/8/6k1/8/4Kp2/8/8 w - - 0 2");

        // Illegal moves
        // Not a piece
        testIllegalMove("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                moveFromSquares("d3", "d4", false, false));
        // Incorrect piece movement
        testIllegalMove("4k3/8/8/8/8/8/7Q/4K3 w - - 0 1",
                moveFromSquares("h2", "a5", false, false));
        testIllegalMove("4k3/8/8/8/1R6/8/8/4K3 w - - 0 1",
                moveFromSquares("b4", "c5", false, false));
        testIllegalMove("4k3/8/6b1/8/8/8/8/4K3 b - - 0 1",
                moveFromSquares("g6", "g2", false, false));
        testIllegalMove("4k2n/8/8/8/8/8/8/4K3 b - - 0 1",
                moveFromSquares("h8", "g7", false, false));
        testIllegalMove("4k3/8/8/8/8/8/8/4K3 w - - 0 1",
                moveFromSquares("e1", "c1", false, false));
        testIllegalMove("4k3/8/8/8/8/6P1/8/4K3 w - - 0 1",
                moveFromSquares("g3", "g5", false, false));
        // Piece blocked
        testIllegalMove("4k3/8/8/8/8/6B1/7Q/4K3 w - - 0 1",
                moveFromSquares("h2", "f4", false, false));
        testIllegalMove("4k3/8/8/6b1/6R1/8/8/4K3 w - - 0 1",
                moveFromSquares("g4", "g6", false, false));
        testIllegalMove("4k3/8/8/6b1/5p2/8/8/4K3 b - - 0 1",
                moveFromSquares("g5", "d2", false, false));
        testIllegalMove("4k3/8/8/8/8/8/4B3/4K3 w - - 0 1",
                moveFromSquares("e1", "e2", false, false));
        testIllegalMove("4k3/4P3/8/8/8/8/8/4K3 w - - 0 1",
                moveFromSquares("e7", "e8", 'Q', false));
        // Not a capture
        testIllegalMove("4kb2/8/8/8/8/6R1/8/4K3 w - - 0 1",
                moveFromSquares("g3", "g5", false, true));
        testIllegalMove("4kb2/8/8/8/8/6R1/8/4K3 b - - 0 1",
                moveFromSquares("f8", "c5", false, true));
        // Not an en passant
        testIllegalMove("4k3/8/5p2/6P1/8/8/8/4K3 w - - 0 1",
                moveFromSquares("g5", "f6", true, true));
        testIllegalMove("4k3/8/8/8/6p1/7P/8/4K3 b - - 0 1",
                moveFromSquares("g4", "h3", true, true));
        // Wrong side to move
        testIllegalMove("4kb2/8/8/8/8/6R1/8/4K3 b - - 0 1",
                moveFromSquares("g3", "g5", false, false));
        testIllegalMove("4kb2/8/8/8/8/6R1/8/4K3 w - - 0 1",
                moveFromSquares("e8", "d8", false, false));
        // Move results in check
        testIllegalMove("4k3/4b3/8/8/8/4R3/8/4K3 b - - 0 1",
                moveFromSquares("e7", "d6", false, false));
        testIllegalMove("4k3/8/8/8/7q/8/5R2/4K3 w - - 0 1",
                moveFromSquares("f2", "e2", false, false));
        testIllegalMove("4k3/7R/8/8/8/8/8/4K3 b - - 0 1",
                moveFromSquares("e8", "d7", false, false));
        testIllegalMove("4k3/8/8/8/8/6b1/5p2/4K3 w - - 0 1",
                moveFromSquares("e1", "f2", false, true));
        testIllegalMove("8/r5PK/k7/8/8/8/8/8 w - - 0 1",
                moveFromSquares("g7", "g8", 'Q', false));
        testIllegalMove("8/8/8/8/k1pP2RK/8/8/8 b - d3 0 1",
                moveFromSquares("c4", "d3", true, true));
        // Illegal Castling
        testIllegalMove("4k3/8/4r3/8/8/8/8/4K2R w K - 0 1",
                new Move('K'));
        testIllegalMove("4k3/8/5r2/8/8/8/8/4K2R w K - 0 1",
                new Move('K'));
        testIllegalMove("4k3/8/8/8/8/8/8/R3K2R w K - 0 1",
                new Move('Q'));
        testIllegalMove("4k1nr/8/8/8/8/8/8/4K3 b k - 0 1",
                new Move('k'));
        testIllegalMove("r3k2r/4N3/8/8/8/8/8/4K3 b q - 0 1",
                new Move('k'));
        testIllegalMove("r3k2r/4N3/8/8/8/8/8/4K3 b q - 0 1",
                new Move('q'));
        // Illegal En passant
        testIllegalMove("4k3/8/8/5pP1/8/8/8/4K3 w - - 0 2",
                moveFromSquares("g5", "f6", true, true));
        testIllegalMove("4k3/8/8/5nP1/8/8/8/4K3 w - - 0 2",
                moveFromSquares("g5", "f6", true, true));
        testIllegalMove("4k3/8/8/8/6Pp/8/8/4K3 b - - 0 2",
                moveFromSquares("h4", "g3", true, true));
        testIllegalMove("4k3/8/8/4P1p1/8/8/8/4K3 w - g6 0 3",
                moveFromSquares("e5", "g6", true, true));

        // Can't undo last move if no move was made
        Board board = new Board();
        assertFalse(board.undoLastMove());
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", board.toFEN());
    }

    /**
     * Asserts that the number of legal moves in the position is equal to expected.
     */
    private void assertLegalCount(String fen, int expected)
            throws MalformedFENException, IllegalBoardException {
        Board board = new Board(fen);
        Set<Move> legalMoves = board.getLegalMoves();
        if (printMoves) {
            System.out.println(board);
            List<String> sorted = new ArrayList<>();
            for (Move move : legalMoves) {
                sorted.add(move.toString());
            }
            Collections.sort(sorted);
            System.out.println("All legal moves: " + sorted);
            System.out.println();
        }
        assertEquals(expected, legalMoves.size(), board.toString());
    }

    /**
     * Asserts that the number of legal moves in the position
     * for the piece at the given square is equal to expected.
     */
    private void assertLegalCount(String fen, String square, int expected)
            throws MalformedFENException, IllegalBoardException {
        Board board = new Board(fen);
        int[] coord = squareToCoords(square);
        Set<Move> legalMoves = board.getLegalMoves(coord[0], coord[1]);
        if (printMoves) {
            System.out.println(board);
            List<String> sorted = new ArrayList<>();
            for (Move move : legalMoves) {
                sorted.add(move.toString());
            }
            Collections.sort(sorted);
            System.out.println("Legal moves for piece at " + square + ": " + sorted);
            System.out.println();
        }
        assertEquals(expected, legalMoves.size(), board.toString());
    }

    @Test
    public void testGetLegalMoves() throws MalformedFENException, IllegalBoardException {
        // Starting position
        assertLegalCount("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 20);
        assertLegalCount("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "a1", 0);
        assertLegalCount("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "a2", 2);
        // Two Kings
        assertLegalCount("8/8/2k5/8/8/5K2/8/8 w - - 0 1", 8);
        assertLegalCount("8/8/2k5/8/8/5K2/8/8 b - - 0 1", 8);

        // Queen
        assertLegalCount("4k3/8/8/8/3Q4/8/8/4K3 w - - 0 1", "d4", 27);
        assertLegalCount("4k3/8/8/8/8/8/8/Q3K3 w - - 0 1", "a1", 17);
        assertLegalCount("4k3/7r/4n3/8/2n1Q1P1/8/2B5/4K3 w - - 0 1", "e4", 18);
        assertLegalCount("Qnrrk3/1B6/8/8/6P1/8/p7/4K3 w - - 0 1", "a8", 7);
        assertLegalCount("Qnrrk3/PB6/8/8/6P1/8/p7/4K3 w - - 0 1", "a8", 1);
        assertLegalCount("4k3/2q5/8/8/8/8/8/5K2 b - - 0 1", "c7", 23);
        // Pinned Queen
        assertLegalCount("4k3/8/4q3/8/8/8/8/4RK2 b - - 0 1", "e6", 6);
        assertLegalCount("3k4/8/8/8/2b5/8/4Q3/5K2 w - - 0 1", "e2", 2);
        // In Check
        assertLegalCount("5r2/8/1k6/8/8/3Q1K2/8/8 w - - 0 1", "d3", 1);
        assertLegalCount("5r2/8/1k6/8/8/5K2/8/7Q w - - 0 1", "h1", 0);
        assertLegalCount("5r2/8/1k6/7Q/8/8/8/5K2 w - - 0 1", "h5", 3);
        assertLegalCount("5rQ1/8/1k6/8/8/8/8/5K2 w - - 0 1", "g8", 2);
        assertLegalCount("8/8/1k6/7q/2N5/8/8/5K2 b - - 0 1", "h5", 0);
        assertLegalCount("8/8/1k6/4q3/8/4B3/8/5K2 b - - 0 1", "e5", 3);

        // Rook
        assertLegalCount("3k4/8/3K4/8/8/8/8/7R w - - 0 1", "h1", 14);
        assertLegalCount("3k4/8/3K4/8/5R2/8/8/8 w - - 0 1", "f4", 14);
        assertLegalCount("3k4/8/3K4/8/8/r7/8/8 b - - 0 1", "a3", 14);
        assertLegalCount("8/8/k7/8/4K3/r3P3/8/8 b - - 0 1", "a3", 8);
        assertLegalCount("8/8/k7/8/4K3/r3p3/8/8 b - - 0 1", "a3", 7);
        // Pinned Rook
        assertLegalCount("8/8/k7/8/r2RK3/8/8/8 w - - 0 1", "d4", 3);
        assertLegalCount("8/rb6/k7/3R4/4K3/8/8/8 w - - 0 1", "d5", 0);
        assertLegalCount("8/4r3/k7/4P3/8/nb2R3/4K3/8 w - - 0 1", "e3", 7);
        // In Check
        assertLegalCount("8/8/1k6/8/2b5/2R5/8/5K2 w - - 0 1", "c3", 2);
        assertLegalCount("8/r7/pk6/8/NN6/8/8/5K2 b - - 0 1", "a7", 0);

        // Bishop
        assertLegalCount("8/8/4k3/8/3BK3/8/8/8 w - - 0 1", "d4", 13);
        assertLegalCount("8/8/6kb/8/4K3/8/8/8 b - - 0 1", "h6", 7);
        assertLegalCount("8/8/6k1/8/4K3/8/8/1B6 w - - 0 1", "b1", 3);
        assertLegalCount("2k5/8/P5p1/8/8/3B4/8/1R1K1n2 w - - 0 1", "d3", 8);
        assertLegalCount("2k5/8/8/8/7p/6pP/5pP1/3K3B w - - 0 1", "h1", 0);
        assertLegalCount("7k/5K2/6B1/6B1/8/8/8/8 w - - 0 1", "g5", 9);
        // Pinned Bishop
        assertLegalCount("8/4k3/4q3/8/8/4B3/4K3/8 w - - 0 1", "e3", 0);
        assertLegalCount("8/6k1/7b/8/8/4B3/3K4/8 w - - 0 1", "e3", 3);
        // In check
        assertLegalCount("8/1k6/p7/8/8/8/5B2/5K1r w - - 0 1", "f2", 1);
        assertLegalCount("8/8/1k5R/6b1/8/8/8/5K2 b - - 0 1", "g5", 2);
        assertLegalCount("8/7b/1k3R2/8/8/8/8/5K2 b - - 0 1", "h7", 0);

        // Knight
        assertLegalCount("4k3/8/8/8/2N5/8/8/4K3 w - - 0 1", "c4", 8);
        assertLegalCount("4k3/8/8/8/8/7n/8/4K3 b - - 0 1", "h3", 4);
        assertLegalCount("N6k/8/8/8/8/8/8/4K3 w - - 0 1", "a8", 2);
        assertLegalCount("2k5/2P1n3/1r3r2/3N4/1n3Q2/2p1b3/8/4K3 w - - 0 1", "d5", 6);
        assertLegalCount("3k4/8/8/8/8/2PPP3/2PNP3/2RKR3 w - - 0 1", "d2", 6);
        // Pinned Knight
        assertLegalCount("4k3/8/4r3/8/8/8/4N3/4K3 w - - 0 1", "e2", 0);
        assertLegalCount("8/2k5/8/4n3/8/8/7Q/4K3 b - - 0 1", "e5", 0);
        // In Check
        assertLegalCount("8/8/8/1n6/8/1k3R2/8/5K2 b - - 0 1", "b5", 1);
        assertLegalCount("8/8/5q2/8/6N1/1k6/8/5K2 w - - 0 1", "g4", 2);
        assertLegalCount("8/8/5q2/8/8/1k3K2/8/6N1 w - - 0 1", "g1", 0);

        // King
        assertLegalCount("8/8/4k3/8/8/8/8/K7 w - - 0 1", "a1", 3);
        assertLegalCount("8/8/4k3/8/8/8/8/4K3 w - - 0 1", "e1", 5);
        assertLegalCount("8/8/4k3/8/8/4K3/8/8 w - - 0 1", "e3", 8);
        assertLegalCount("8/8/4k3/8/4P3/4K3/8/8 w - - 0 1", "e3", 7);
        assertLegalCount("4k3/3bNR2/8/8/4P3/4K3/8/8 b - - 0 1", "e8", 2);
        assertLegalCount("8/8/4k3/4b3/4P3/4K3/8/8 w - - 0 1", "e3", 5);
        assertLegalCount("8/3k4/8/5b1r/8/6K1/8/4n3 w - - 0 1", "g3", 2);
        // In Check/Checkmated
        assertLegalCount("8/3k4/4r3/8/8/4K3/8/8 w - - 0 1", "e3", 6);
        assertLegalCount("8/3k4/8/8/8/8/4PPP1/1r3K2 w - - 0 1", "f1", 0);
        assertLegalCount("8/8/8/6n1/8/1k3K2/8/6N1 w - - 0 1", "f3", 7);
        assertLegalCount("8/8/8/6n1/8/1k2NK2/4RB2/8 w - - 0 1", "f3", 4);
        // Castling
        assertLegalCount("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", "e1", 7);
        assertLegalCount("r3k2r/8/8/8/8/8/8/R3K2R w Qkq - 0 1", "e1", 6);
        assertLegalCount("r3k2r/8/8/8/8/8/8/R3K2R w Kkq - 0 1", "e1", 6);
        assertLegalCount("r3k2r/8/8/8/8/8/8/R3K2R w kq - 0 1", "e1", 5);
        assertLegalCount("r3k1r1/8/8/8/8/8/8/R3K2R b KQq - 0 1", "e8", 6);
        // Can't castle when in check
        assertLegalCount("r3k3/4r3/8/8/8/8/8/R3K2R w KQq - 0 1", "e1", 4);
        // Can't castle through check
        assertLegalCount("r3k2r/8/8/5R2/8/8/8/4K3 b kq - 0 1", "e8", 4);
        assertLegalCount("r3k2r/6P1/8/8/8/8/8/1R2K2R b Kkq - 0 1", "e8", 5);
        // Can't castle when landing square is a check
        assertLegalCount("r3k2r/8/8/8/8/8/7p/R3K2R w KQ - 0 1", "e1", 6);
        assertLegalCount("r3k2r/8/8/8/5b2/8/7p/R3K2R w KQkq - 0 1", "e1", 4);
        // Can't castle when blocked by pieces
        assertLegalCount("r3k1nr/8/8/8/8/8/8/R3K2R b kq - 0 1", "e8", 6);
        assertLegalCount("r3kB1r/8/8/8/8/8/8/R3K2R b KQkq - 0 1", "e8", 5);
        assertLegalCount("r3k2r/8/8/8/8/8/8/R2QK1nR w KQkq - 0 1", "e1", 3);
        // Can castle when rook passes through a square controlled by opponent
        assertLegalCount("r3k2r/8/8/8/8/8/8/1R2K2R b Kkq - 0 1", "e8", 7);

        // Pawn
        assertLegalCount("4k3/8/8/8/4P3/8/8/4K3 w - - 0 1", "e4", 1);
        assertLegalCount("4k3/8/8/8/8/8/5P2/4K3 w - - 0 1", "f2", 2);
        assertLegalCount("4k3/8/p7/8/8/8/8/4K3 b - - 0 1", "a6", 1);
        assertLegalCount("4k3/7p/8/8/8/8/8/4K3 b - - 0 1", "h7", 2);
        assertLegalCount("4k3/8/2r5/2P5/8/8/8/4K3 w - - 0 1", "c5", 0);
        assertLegalCount("4k3/8/8/8/2B5/8/2P5/4K3 w - - 0 1", "c2", 1);
        assertLegalCount("4k3/8/8/8/7p/7R/8/4K3 b - - 0 1", "h4", 0);
        assertLegalCount("4k3/2p5/8/2n5/8/8/8/4K3 b - - 0 1", "c7", 1);
        // Pawn captures
        assertLegalCount("4k3/8/8/8/8/3r4/2P5/4K3 w - - 0 1", "c2", 3);
        assertLegalCount("4k3/8/8/8/8/5b1n/6P1/4K3 w - - 0 1", "g2", 4);
        assertLegalCount("4k3/8/8/8/6r1/5b1n/6P1/4K3 w - - 0 1", "g2", 3);
        assertLegalCount("4k3/nb6/P7/8/8/8/8/4K3 w - - 0 1", "a6", 1);
        assertLegalCount("4k3/1p6/B1N5/8/8/8/8/4K3 b - - 0 1", "b7", 4);
        assertLegalCount("4k3/8/8/6p1/6rB/8/8/4K3 b - - 0 1", "g5", 1);
        assertLegalCount("4k3/8/8/6p1/5qrB/8/8/4K3 b - - 0 1", "g5", 1);
        // Pinned Pawn
        assertLegalCount("6k1/6r1/8/6P1/6K1/8/8/8 w - - 0 1", "g5", 1);
        assertLegalCount("6k1/6r1/5b1r/6P1/6K1/8/8/8 w - - 0 1", "g5", 1);
        assertLegalCount("6k1/6r1/8/8/8/5b1r/6P1/6K1 w - - 0 1", "g2", 2);
        assertLegalCount("6k1/8/8/2b5/8/8/5P2/6K1 w - - 0 1", "f2", 0);
        assertLegalCount("5k2/5p2/4R1B1/8/8/8/5R2/6K1 b - - 0 1", "f7", 2);
        assertLegalCount("5k2/8/5p2/4R1B1/8/8/5R2/6K1 b - - 0 1", "f6", 1);
        assertLegalCount("5k2/4p3/8/8/1Q6/8/8/6K1 b - - 0 1", "e7", 0);
        assertLegalCount("5k2/4p3/3Q4/8/8/8/8/6K1 b - - 0 1", "e7", 1);
        // In Check
        assertLegalCount("5k2/8/8/8/4n3/5P2/5K2/8 w - - 0 1", "f3", 1);
        assertLegalCount("5k2/5p2/6N1/8/8/8/5K2/8 b - - 0 1", "f7", 1);
        assertLegalCount("8/5p2/8/1k5R/8/8/5K2/8 b - - 0 1", "f7", 1);
        // Promotions
        assertLegalCount("8/3P4/8/1k6/8/8/5K2/8 w - - 0 1", "d7", 4);
        assertLegalCount("3n1b2/4P3/8/1k6/8/8/5K2/8 w - - 0 1", "e7", 12);
        assertLegalCount("3n1r2/4P3/8/1k6/8/8/5K2/8 w - - 0 1", "e7", 4);
        assertLegalCount("8/8/8/1k6/8/8/1p3K2/8 b - - 0 1", "b2", 4);
        assertLegalCount("8/8/8/1k6/8/8/2p2K2/1NnR4 b - - 0 1", "c2", 8);
        assertLegalCount("8/8/8/1k6/8/8/2p2K2/1QnR4 b - - 0 1", "c2", 4);
        assertLegalCount("8/8/8/8/8/8/2p2K2/1Q5k b - - 0 1", "c2", 8);
        assertLegalCount("8/2KP2r1/7k/8/8/8/8/8 w - - 0 1", "d7", 0);
        // En passant
        assertLegalCount("rnbqkbnr/pppp1ppp/8/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 0 2", "d5", 2);
        assertLegalCount("4k3/8/8/8/6pP/8/8/4K3 b - h3 0 1", "g4", 2);
        assertLegalCount("4k3/8/8/2PpP3/8/8/8/4K3 w - d6 0 2", 9);
        assertLegalCount("4k3/8/8/8/5pPp/8/8/4K3 b - g3 0 1", 9);
        assertLegalCount("8/8/3B4/8/5pP1/8/2K4k/8 b - g3 0 1", "f4", 1);
        // Can only en passant a pawn
        assertLegalCount("8/4k3/8/2Pr4/8/8/2K5/8 w - - 0 2", "c5", 1);
        assertLegalCount("8/4k3/8/8/5pB1/8/2K5/8 b - - 0 2", "f4", 1);
        // En passant to resolve check
        assertLegalCount("5k2/8/8/5Pp1/5K2/8/8/8 w - g6 0 2", "f5", 1);
        assertLegalCount("8/8/8/6k1/4pP2/8/8/4K3 b - f3 0 1", "e4", 1);
        // Cannot en passant if opponent pawn hasn't just moved
        assertLegalCount("rnbqkbnr/ppp1pppp/8/8/3pP3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1", "d4", 1);
        assertLegalCount("rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1", "e5", 1);
        // Cannot en passant if doing so leaves your king in check
        assertLegalCount("2r5/4k3/8/2Pp4/8/8/2K5/8 w - d6 0 2", "c5", 1);
        assertLegalCount("8/8/8/8/5pP1/8/3R3k/4K3 b - g3 0 1", "f4", 0);
        assertLegalCount("4k3/8/8/r4pPK/8/8/8/8 w - f6 0 2", "g5", 1);
        assertLegalCount("8/8/8/8/k2Pp2Q/8/8/4K3 b - d3 0 1", "e4", 1);
        // Others
        assertLegalCount("rnbqkbnr/pppppppp/5N2/8/8/8/PPPPPPPP/RNBQKB1R b KQkq - 0 1", 3);
        assertLegalCount("rnbqkbnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNBQR1K1 b kq - 0 1", 3);
        assertLegalCount("rnbqkbnr/pppp1ppp/5N2/8/8/8/PPPP1PPP/RNBQR1K1 b kq - 0 1", 0);
        assertLegalCount("r6K/PPPPPPPP/8/8/8/8/8/4k3 w - - 0 1", 28);
        // Largest number of legal moves
        assertLegalCount("R6R/3Q4/1Q4Q1/4Q3/2Q4Q/Q4Q2/pp1Q4/kBNN1KB1 w - - 0 1", 218);
        // En passant mate
        assertLegalCount("rn1q1bkr/pppp2pp/8/4P3/2B5/8/8/4K3 b - - 0 1", 1);
        assertLegalCount("rn1q1bkr/ppp3pp/8/3pP3/2B5/8/8/4K3 w - d6 0 2", 15);
    }

    @Test
    public void testGetWinner() throws MalformedFENException, IllegalBoardException {
        // TODO
    }
}
