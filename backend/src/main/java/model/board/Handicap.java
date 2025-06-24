package model.board;

public enum Handicap {
    NONE("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    WHITE_QUEEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNB1KBNR w KQkq - 0 1"),
    WHITE_A_ROOK("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR w KQkq - 0 1"),
    WHITE_H_ROOK("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBN1 w KQkq - 0 1"),
    WHITE_B_KNIGHT("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R1BQKBNR w KQkq - 0 1"),
    WHITE_G_KNIGHT("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKB1R w KQkq - 0 1"),
    WHITE_C_BISHOP("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RN1QKBNR w KQkq - 0 1"),
    WHITE_F_BISHOP("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQK1NR w KQkq - 0 1"),
    WHITE_BOTH_ROOKS("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBN1 w KQkq - 0 1"),
    WHITE_QUEEN_BOTH_ROOKS("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NB1KBN1 w KQkq - 0 1"),
    BLACK_QUEEN("rnb1kbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_A_ROOK("1nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_H_ROOK("rnbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_B_KNIGHT("r1bqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_G_KNIGHT("rnbqkb1r/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_C_BISHOP("rn1qkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_F_BISHOP("rnbqk1nr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_BOTH_ROOKS("1nbqkbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    BLACK_QUEEN_BOTH_ROOKS("1nb1kbn1/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    public final String startPos;  // starting position for a given handicap

    Handicap(String startPos) {
        this.startPos = startPos;
    }
}
