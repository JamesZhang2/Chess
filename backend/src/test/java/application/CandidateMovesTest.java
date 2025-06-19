package application;

import model.board.BitmapBoard;
import model.board.Board;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandidateMovesTest {
    @Test
    void basicTest() throws IllegalBoardException, MalformedFENException {
        Board board0 = new BitmapBoard();
        CandidateMoves c0 = new CandidateMoves(board0, "a1");
        assertEquals(new HashSet<>(), new HashSet<>(c0.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c0.legalPromotions));
        CandidateMoves c1 = new CandidateMoves(board0, "e2");
        assertEquals(new HashSet<>(List.of("e3", "e4")), new HashSet<>(c1.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c1.legalPromotions));
        CandidateMoves c2 = new CandidateMoves(board0, "g1");
        assertEquals(new HashSet<>(List.of("f3", "h3")), new HashSet<>(c2.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c2.legalPromotions));
        CandidateMoves c3 = new CandidateMoves(board0, "e7");
        assertEquals(new HashSet<>(), new HashSet<>(c3.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c3.legalPromotions));
        CandidateMoves c4 = new CandidateMoves(board0, "b5");
        assertEquals(new HashSet<>(), new HashSet<>(c4.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c4.legalPromotions));

        Board board1 = new BitmapBoard("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        CandidateMoves c5 = new CandidateMoves(board1, "f8");
        assertEquals(new HashSet<>(List.of("e7", "d6", "c5", "b4", "a3")), new HashSet<>(c5.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c5.legalPromotions));
        CandidateMoves c6 = new CandidateMoves(board1, "e5");
        assertEquals(new HashSet<>(), new HashSet<>(c6.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c6.legalPromotions));
        CandidateMoves c7 = new CandidateMoves(board1, "h1");
        assertEquals(new HashSet<>(), new HashSet<>(c7.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c7.legalPromotions));

        Board board2 = new BitmapBoard("r3k2r/ppp2ppp/8/8/8/8/PPP2PPP/R3K2R w Q - 0 1");
        // white can only castle queenside
        CandidateMoves c8 = new CandidateMoves(board2, "e1");
        assertEquals(new HashSet<>(List.of("c1", "d1", "d2", "e2", "f1")), new HashSet<>(c8.legalDests));
        assertEquals(new HashSet<>(), new HashSet<>(c8.legalPromotions));

        Board board3 = new BitmapBoard("8/8/8/8/8/1K6/4p3/3R3k b - - 0 1");
        CandidateMoves c9 = new CandidateMoves(board3, "e2");
        assertEquals(new HashSet<>(List.of("d1", "e1")), new HashSet<>(c9.legalDests));
        assertEquals(new HashSet<>(List.of("d1", "e1")), new HashSet<>(c9.legalPromotions));
    }
}