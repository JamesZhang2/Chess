import model.Board;
import model.IllegalBoardException;
import model.MalformedFENException;
import model.Move;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {

    private final boolean printMoves = true;

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
                "2nkb3/2ppp3/8/8/8/2RQR3/3K4/8 b - - 0 1"
        };
        for (String fen : notInCheckFENs) {
            Board board = new Board(fen);
            assert !board.isInCheck();
        }
    }

    @Test
    public void testMove() throws MalformedFENException, IllegalBoardException {

    }

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

    private void assertLegalCount(String fen, String square, int expected)
            throws MalformedFENException, IllegalBoardException {
        Board board = new Board(fen);
        int row = square.charAt(1) - '1';
        int col = square.charAt(0) - 'a';
        Set<Move> legalMoves = board.getLegalMoves(row, col);
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
        // Bishop
        // Knight
        // King
        // Pawn
    }

    @Test
    public void testIsLegal() throws MalformedFENException, IllegalBoardException {

    }

    @Test
    public void testGetWinner() throws MalformedFENException, IllegalBoardException {

    }
}
