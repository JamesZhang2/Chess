package model.board;

import model.Util;

/**
 * A class for computing and storing lookup tables for Bitmap boards.
 * The lookup tables are pre-computed before the main program runs to save time
 * (with a small tradeoff in memory).
 */
public class Lookup {
    public static final int PAWN_MIN_IDX = 8;  // minimum index that a pawn can be at
    public static final int PAWN_MAX_IDX = 55;  // maximum index that a pawn can be at

    public static final long[] WHITE_PAWN_ATTACK = {
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000020000L,
            0x0000000000050000L,
            0x00000000000A0000L,
            0x0000000000140000L,
            0x0000000000280000L,
            0x0000000000500000L,
            0x0000000000A00000L,
            0x0000000000400000L,
            0x0000000002000000L,
            0x0000000005000000L,
            0x000000000A000000L,
            0x0000000014000000L,
            0x0000000028000000L,
            0x0000000050000000L,
            0x00000000A0000000L,
            0x0000000040000000L,
            0x0000000200000000L,
            0x0000000500000000L,
            0x0000000A00000000L,
            0x0000001400000000L,
            0x0000002800000000L,
            0x0000005000000000L,
            0x000000A000000000L,
            0x0000004000000000L,
            0x0000020000000000L,
            0x0000050000000000L,
            0x00000A0000000000L,
            0x0000140000000000L,
            0x0000280000000000L,
            0x0000500000000000L,
            0x0000A00000000000L,
            0x0000400000000000L,
            0x0002000000000000L,
            0x0005000000000000L,
            0x000A000000000000L,
            0x0014000000000000L,
            0x0028000000000000L,
            0x0050000000000000L,
            0x00A0000000000000L,
            0x0040000000000000L,
            0x0200000000000000L,
            0x0500000000000000L,
            0x0A00000000000000L,
            0x1400000000000000L,
            0x2800000000000000L,
            0x5000000000000000L,
            0xA000000000000000L,
            0x4000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
    };

    public static final long[] BLACK_PAWN_ATTACK = {
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000002L,
            0x0000000000000005L,
            0x000000000000000AL,
            0x0000000000000014L,
            0x0000000000000028L,
            0x0000000000000050L,
            0x00000000000000A0L,
            0x0000000000000040L,
            0x0000000000000200L,
            0x0000000000000500L,
            0x0000000000000A00L,
            0x0000000000001400L,
            0x0000000000002800L,
            0x0000000000005000L,
            0x000000000000A000L,
            0x0000000000004000L,
            0x0000000000020000L,
            0x0000000000050000L,
            0x00000000000A0000L,
            0x0000000000140000L,
            0x0000000000280000L,
            0x0000000000500000L,
            0x0000000000A00000L,
            0x0000000000400000L,
            0x0000000002000000L,
            0x0000000005000000L,
            0x000000000A000000L,
            0x0000000014000000L,
            0x0000000028000000L,
            0x0000000050000000L,
            0x00000000A0000000L,
            0x0000000040000000L,
            0x0000000200000000L,
            0x0000000500000000L,
            0x0000000A00000000L,
            0x0000001400000000L,
            0x0000002800000000L,
            0x0000005000000000L,
            0x000000A000000000L,
            0x0000004000000000L,
            0x0000020000000000L,
            0x0000050000000000L,
            0x00000A0000000000L,
            0x0000140000000000L,
            0x0000280000000000L,
            0x0000500000000000L,
            0x0000A00000000000L,
            0x0000400000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
    };

    public static final long[] KNIGHT_ATTACK = {
            0x0000000000020400L,
            0x0000000000050800L,
            0x00000000000A1100L,
            0x0000000000142200L,
            0x0000000000284400L,
            0x0000000000508800L,
            0x0000000000A01000L,
            0x0000000000402000L,
            0x0000000002040004L,
            0x0000000005080008L,
            0x000000000A110011L,
            0x0000000014220022L,
            0x0000000028440044L,
            0x0000000050880088L,
            0x00000000A0100010L,
            0x0000000040200020L,
            0x0000000204000402L,
            0x0000000508000805L,
            0x0000000A1100110AL,
            0x0000001422002214L,
            0x0000002844004428L,
            0x0000005088008850L,
            0x000000A0100010A0L,
            0x0000004020002040L,
            0x0000020400040200L,
            0x0000050800080500L,
            0x00000A1100110A00L,
            0x0000142200221400L,
            0x0000284400442800L,
            0x0000508800885000L,
            0x0000A0100010A000L,
            0x0000402000204000L,
            0x0002040004020000L,
            0x0005080008050000L,
            0x000A1100110A0000L,
            0x0014220022140000L,
            0x0028440044280000L,
            0x0050880088500000L,
            0x00A0100010A00000L,
            0x0040200020400000L,
            0x0204000402000000L,
            0x0508000805000000L,
            0x0A1100110A000000L,
            0x1422002214000000L,
            0x2844004428000000L,
            0x5088008850000000L,
            0xA0100010A0000000L,
            0x4020002040000000L,
            0x0400040200000000L,
            0x0800080500000000L,
            0x1100110A00000000L,
            0x2200221400000000L,
            0x4400442800000000L,
            0x8800885000000000L,
            0x100010A000000000L,
            0x2000204000000000L,
            0x0004020000000000L,
            0x0008050000000000L,
            0x00110A0000000000L,
            0x0022140000000000L,
            0x0044280000000000L,
            0x0088500000000000L,
            0x0010A00000000000L,
            0x0020400000000000L,
    };

    public static final long[] KING_ATTACK = {
            0x0000000000000302L,
            0x0000000000000705L,
            0x0000000000000E0AL,
            0x0000000000001C14L,
            0x0000000000003828L,
            0x0000000000007050L,
            0x000000000000E0A0L,
            0x000000000000C040L,
            0x0000000000030203L,
            0x0000000000070507L,
            0x00000000000E0A0EL,
            0x00000000001C141CL,
            0x0000000000382838L,
            0x0000000000705070L,
            0x0000000000E0A0E0L,
            0x0000000000C040C0L,
            0x0000000003020300L,
            0x0000000007050700L,
            0x000000000E0A0E00L,
            0x000000001C141C00L,
            0x0000000038283800L,
            0x0000000070507000L,
            0x00000000E0A0E000L,
            0x00000000C040C000L,
            0x0000000302030000L,
            0x0000000705070000L,
            0x0000000E0A0E0000L,
            0x0000001C141C0000L,
            0x0000003828380000L,
            0x0000007050700000L,
            0x000000E0A0E00000L,
            0x000000C040C00000L,
            0x0000030203000000L,
            0x0000070507000000L,
            0x00000E0A0E000000L,
            0x00001C141C000000L,
            0x0000382838000000L,
            0x0000705070000000L,
            0x0000E0A0E0000000L,
            0x0000C040C0000000L,
            0x0003020300000000L,
            0x0007050700000000L,
            0x000E0A0E00000000L,
            0x001C141C00000000L,
            0x0038283800000000L,
            0x0070507000000000L,
            0x00E0A0E000000000L,
            0x00C040C000000000L,
            0x0302030000000000L,
            0x0705070000000000L,
            0x0E0A0E0000000000L,
            0x1C141C0000000000L,
            0x3828380000000000L,
            0x7050700000000000L,
            0xE0A0E00000000000L,
            0xC040C00000000000L,
            0x0203000000000000L,
            0x0507000000000000L,
            0x0A0E000000000000L,
            0x141C000000000000L,
            0x2838000000000000L,
            0x5070000000000000L,
            0xA0E0000000000000L,
            0x40C0000000000000L,
    };

    // A white pawn at index idx is a doubled pawn
    // if (bitmap of white pawns & WHITE_DOUBLED_PAWN_MASK[idx]) is nonzero
    public static final long[] WHITE_DOUBLED_PAWN_MASK = {
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0001010101010000L,
            0x0002020202020000L,
            0x0004040404040000L,
            0x0008080808080000L,
            0x0010101010100000L,
            0x0020202020200000L,
            0x0040404040400000L,
            0x0080808080800000L,
            0x0001010101000000L,
            0x0002020202000000L,
            0x0004040404000000L,
            0x0008080808000000L,
            0x0010101010000000L,
            0x0020202020000000L,
            0x0040404040000000L,
            0x0080808080000000L,
            0x0001010100000000L,
            0x0002020200000000L,
            0x0004040400000000L,
            0x0008080800000000L,
            0x0010101000000000L,
            0x0020202000000000L,
            0x0040404000000000L,
            0x0080808000000000L,
            0x0001010000000000L,
            0x0002020000000000L,
            0x0004040000000000L,
            0x0008080000000000L,
            0x0010100000000000L,
            0x0020200000000000L,
            0x0040400000000000L,
            0x0080800000000000L,
            0x0001000000000000L,
            0x0002000000000000L,
            0x0004000000000000L,
            0x0008000000000000L,
            0x0010000000000000L,
            0x0020000000000000L,
            0x0040000000000000L,
            0x0080000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
    };

    public static final long[] BLACK_DOUBLED_PAWN_MASK = {
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000100L,
            0x0000000000000200L,
            0x0000000000000400L,
            0x0000000000000800L,
            0x0000000000001000L,
            0x0000000000002000L,
            0x0000000000004000L,
            0x0000000000008000L,
            0x0000000000010100L,
            0x0000000000020200L,
            0x0000000000040400L,
            0x0000000000080800L,
            0x0000000000101000L,
            0x0000000000202000L,
            0x0000000000404000L,
            0x0000000000808000L,
            0x0000000001010100L,
            0x0000000002020200L,
            0x0000000004040400L,
            0x0000000008080800L,
            0x0000000010101000L,
            0x0000000020202000L,
            0x0000000040404000L,
            0x0000000080808000L,
            0x0000000101010100L,
            0x0000000202020200L,
            0x0000000404040400L,
            0x0000000808080800L,
            0x0000001010101000L,
            0x0000002020202000L,
            0x0000004040404000L,
            0x0000008080808000L,
            0x0000010101010100L,
            0x0000020202020200L,
            0x0000040404040400L,
            0x0000080808080800L,
            0x0000101010101000L,
            0x0000202020202000L,
            0x0000404040404000L,
            0x0000808080808000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
    };

    // A pawn at index idx is an isolated pawn
    // if (bitmap of friendly pawns & ISOLATED_PAWN_MASK[idx]) is zero
    public static final long[] ISOLATED_PAWN_MASK = {
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0202020202020202L,
            0x0505050505050505L,
            0x0A0A0A0A0A0A0A0AL,
            0x1414141414141414L,
            0x2828282828282828L,
            0x5050505050505050L,
            0xA0A0A0A0A0A0A0A0L,
            0x4040404040404040L,
            0x0202020202020202L,
            0x0505050505050505L,
            0x0A0A0A0A0A0A0A0AL,
            0x1414141414141414L,
            0x2828282828282828L,
            0x5050505050505050L,
            0xA0A0A0A0A0A0A0A0L,
            0x4040404040404040L,
            0x0202020202020202L,
            0x0505050505050505L,
            0x0A0A0A0A0A0A0A0AL,
            0x1414141414141414L,
            0x2828282828282828L,
            0x5050505050505050L,
            0xA0A0A0A0A0A0A0A0L,
            0x4040404040404040L,
            0x0202020202020202L,
            0x0505050505050505L,
            0x0A0A0A0A0A0A0A0AL,
            0x1414141414141414L,
            0x2828282828282828L,
            0x5050505050505050L,
            0xA0A0A0A0A0A0A0A0L,
            0x4040404040404040L,
            0x0202020202020202L,
            0x0505050505050505L,
            0x0A0A0A0A0A0A0A0AL,
            0x1414141414141414L,
            0x2828282828282828L,
            0x5050505050505050L,
            0xA0A0A0A0A0A0A0A0L,
            0x4040404040404040L,
            0x0202020202020202L,
            0x0505050505050505L,
            0x0A0A0A0A0A0A0A0AL,
            0x1414141414141414L,
            0x2828282828282828L,
            0x5050505050505050L,
            0xA0A0A0A0A0A0A0A0L,
            0x4040404040404040L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
    };

    // A white pawn at index idx is a passed pawn
    // if it's not (the back sibling of) a doubled pawn
    // and (bitmap of enemy pawns & WHITE_PASSED_PAWN_MASK[idx]) is zero
    public static final long[] WHITE_PASSED_PAWN_MASK = {
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0003030303030000L,
            0x0007070707070000L,
            0x000E0E0E0E0E0000L,
            0x001C1C1C1C1C0000L,
            0x0038383838380000L,
            0x0070707070700000L,
            0x00E0E0E0E0E00000L,
            0x00C0C0C0C0C00000L,
            0x0003030303000000L,
            0x0007070707000000L,
            0x000E0E0E0E000000L,
            0x001C1C1C1C000000L,
            0x0038383838000000L,
            0x0070707070000000L,
            0x00E0E0E0E0000000L,
            0x00C0C0C0C0000000L,
            0x0003030300000000L,
            0x0007070700000000L,
            0x000E0E0E00000000L,
            0x001C1C1C00000000L,
            0x0038383800000000L,
            0x0070707000000000L,
            0x00E0E0E000000000L,
            0x00C0C0C000000000L,
            0x0003030000000000L,
            0x0007070000000000L,
            0x000E0E0000000000L,
            0x001C1C0000000000L,
            0x0038380000000000L,
            0x0070700000000000L,
            0x00E0E00000000000L,
            0x00C0C00000000000L,
            0x0003000000000000L,
            0x0007000000000000L,
            0x000E000000000000L,
            0x001C000000000000L,
            0x0038000000000000L,
            0x0070000000000000L,
            0x00E0000000000000L,
            0x00C0000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
    };

    public static final long[] BLACK_PASSED_PAWN_MASK = {
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000300L,
            0x0000000000000700L,
            0x0000000000000E00L,
            0x0000000000001C00L,
            0x0000000000003800L,
            0x0000000000007000L,
            0x000000000000E000L,
            0x000000000000C000L,
            0x0000000000030300L,
            0x0000000000070700L,
            0x00000000000E0E00L,
            0x00000000001C1C00L,
            0x0000000000383800L,
            0x0000000000707000L,
            0x0000000000E0E000L,
            0x0000000000C0C000L,
            0x0000000003030300L,
            0x0000000007070700L,
            0x000000000E0E0E00L,
            0x000000001C1C1C00L,
            0x0000000038383800L,
            0x0000000070707000L,
            0x00000000E0E0E000L,
            0x00000000C0C0C000L,
            0x0000000303030300L,
            0x0000000707070700L,
            0x0000000E0E0E0E00L,
            0x0000001C1C1C1C00L,
            0x0000003838383800L,
            0x0000007070707000L,
            0x000000E0E0E0E000L,
            0x000000C0C0C0C000L,
            0x0000030303030300L,
            0x0000070707070700L,
            0x00000E0E0E0E0E00L,
            0x00001C1C1C1C1C00L,
            0x0000383838383800L,
            0x0000707070707000L,
            0x0000E0E0E0E0E000L,
            0x0000C0C0C0C0C000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
            0x0000000000000000L,
    };

    /**
     * Generate the lookup table for pawn attacks
     */
    private static void computePawnAttack(boolean white) {
        long[] bitmaps = new long[64];
        // Can ignore rank 1 and rank 8
        for (int idx = PAWN_MIN_IDX; idx <= PAWN_MAX_IDX; idx++) {
            if (idx % 8 != 0) {
                // not a file
                bitmaps[idx] |= white ? 1L << (idx + 7) : 1L << (idx - 9);
            }
            if (idx % 8 != 7) {
                // not h file
                bitmaps[idx] |= white ? 1L << (idx + 9) : 1L << (idx - 7);
            }
        }
        String name = white ? "WHITE_PAWN_ATTACK" : "BLACK_PAWN_ATTACK";
        printResults(name, bitmaps);
    }

    /**
     * Generate the lookup table for knight attacks
     */
    private static void computeKnightAttack() {
        long[] bitmaps = new long[64];
        for (int idx = 0; idx < 64; idx++) {
            // N, E, S, W = north, east, south, west
            boolean nne, nee, see, sse, ssw, sww, nww, nnw;
            nne = nee = see = sse = ssw = sww = nww = nnw = true;
            long bitmap = 0;
            if (idx % 8 == 0) {
                // a file
                ssw = sww = nww = nnw = false;
            } else if (idx % 8 == 1) {
                // b file
                sww = nww = false;
            } else if (idx % 8 == 6) {
                // g file
                nee = see = false;
            } else if (idx % 8 == 7) {
                // h file
                nne = nee = see = sse = false;
            }

            if (idx / 8 == 0) {
                // rank 1
                see = sse = ssw = sww = false;
            } else if (idx / 8 == 1) {
                // rank 2
                sse = ssw = false;
            } else if (idx / 8 == 6) {
                // rank 7
                nne = nnw = false;
            } else if (idx / 8 == 7) {
                // rank 8
                nne = nee = nww = nnw = false;
            }

            if (nne)
                bitmap |= 1L << (idx + 17);
            if (nee)
                bitmap |= 1L << (idx + 10);
            if (see)
                bitmap |= 1L << (idx - 6);
            if (sse)
                bitmap |= 1L << (idx - 15);
            if (ssw)
                bitmap |= 1L << (idx - 17);
            if (sww)
                bitmap |= 1L << (idx - 10);
            if (nww)
                bitmap |= 1L << (idx + 6);
            if (nnw)
                bitmap |= 1L << (idx + 15);
            bitmaps[idx] = bitmap;
        }
        printResults("KNIGHT_ATTACK", bitmaps);
    }

    /**
     * Generate the lookup table for king attacks
     */
    private static void computeKingAttack() {
        long[] bitmaps = new long[64];
        for (int idx = 0; idx < 64; idx++) {
            // N, E, S, W = north, east, south, west
            boolean n, ne, e, se, s, sw, w, nw;
            n = ne = e = se = s = sw = w = nw = true;
            long bitmap = 0;
            if (idx % 8 == 0) {
                // a file
                nw = w = sw = false;
            } else if (idx % 8 == 7) {
                // h file
                ne = e = se = false;
            }

            if (idx / 8 == 0) {
                // rank 1
                sw = s = se = false;
            } else if (idx / 8 == 7) {
                nw = n = ne = false;
            }

            if (n)
                bitmap |= 1L << (idx + 8);
            if (ne)
                bitmap |= 1L << (idx + 9);
            if (e)
                bitmap |= 1L << (idx + 1);
            if (se)
                bitmap |= 1L << (idx - 7);
            if (s)
                bitmap |= 1L << (idx - 8);
            if (sw)
                bitmap |= 1L << (idx - 9);
            if (w)
                bitmap |= 1L << (idx - 1);
            if (nw)
                bitmap |= 1L << (idx + 7);
            bitmaps[idx] = bitmap;
        }
        printResults("KING_ATTACK", bitmaps);
    }

    /**
     * Generate the lookup tables for pawn masks for doubled pawns
     */
    private static void computeDoubledPawnMasks() {
        long[] whiteBitmaps = new long[64];
        long[] blackBitmaps = new long[64];
        for (int idx = PAWN_MIN_IDX; idx <= PAWN_MAX_IDX; idx++) {
            long whiteBitmap = 0;
            long blackBitmap = 0;
            for (int cur = idx + 8; cur <= PAWN_MAX_IDX; cur += 8) {
                whiteBitmap |= 1L << cur;
            }
            for (int cur = idx - 8; cur >= PAWN_MIN_IDX; cur -= 8) {
                blackBitmap |= 1L << cur;
            }
            whiteBitmaps[idx] = whiteBitmap;
            blackBitmaps[idx] = blackBitmap;
        }
        printResults("WHITE_DOUBLED_PAWN_MASK", whiteBitmaps);
        printResults("BLACK_DOUBLED_PAWN_MASK", blackBitmaps);
    }

    /**
     * Generate the lookup tables for pawn masks for isolated pawns
     */
    private static void computeIsolatedPawnMasks() {
        long[] bitmaps = new long[64];
        for (int idx = PAWN_MIN_IDX; idx <= PAWN_MAX_IDX; idx++) {
            long bitmap = 0;
            if (idx % 8 != 0) {
                // not a file, add left
                for (int cur = idx % 8 - 1; cur < 64; cur += 8) {
                    bitmap |= 1L << cur;
                }
            }
            if (idx % 8 != 7) {
                // not h file, add right
                for (int cur = idx % 8 + 1; cur < 64; cur += 8) {
                    bitmap |= 1L << cur;
                }
            }
            bitmaps[idx] = bitmap;
        }
        printResults("ISOLATED_PAWN_MASK", bitmaps);
    }

    /**
     * Generate the lookup tables for pawn masks for passed pawns
     */
    private static void computePassedPawnMasks() {
        long[] whiteBitmaps = new long[64];
        long[] blackBitmaps = new long[64];
        for (int idx = PAWN_MIN_IDX; idx <= PAWN_MAX_IDX; idx++) {
            long whiteBitmap = 0;
            long blackBitmap = 0;
            if (idx % 8 != 0) {
                // not a file, add left
                for (int cur = idx + 7; cur <= PAWN_MAX_IDX; cur += 8) {
                    whiteBitmap |= 1L << cur;
                }
                for (int cur = idx - 9; cur >= PAWN_MIN_IDX; cur -= 8) {
                    blackBitmap |= 1L << cur;
                }
            }
            for (int cur = idx + 8; cur <= PAWN_MAX_IDX; cur += 8) {
                whiteBitmap |= 1L << cur;
            }
            for (int cur = idx - 8; cur >= PAWN_MIN_IDX; cur -= 8) {
                blackBitmap |= 1L << cur;
            }
            if (idx % 8 != 7) {
                // not h file, add right
                for (int cur = idx + 9; cur <= PAWN_MAX_IDX; cur += 8) {
                    whiteBitmap |= 1L << cur;
                }
                for (int cur = idx - 7; cur >= PAWN_MIN_IDX; cur -= 8) {
                    blackBitmap |= 1L << cur;
                }
            }
            whiteBitmaps[idx] = whiteBitmap;
            blackBitmaps[idx] = blackBitmap;
        }
        printResults("WHITE_PASSED_PAWN_MASK", whiteBitmaps);
        printResults("BLACK_PASSED_PAWN_MASK", blackBitmaps);
    }

    /**
     * Print the array of bitmaps in correct Java syntax
     */
    private static void printResults(String name, long[] bitmaps) {
        StringBuilder sb = new StringBuilder();
        sb.append("public static final long[] ").append(name).append(" = {\n");
        for (long bitmap : bitmaps) {
            sb.append(String.format("0x%016XL", bitmap)).append(",\n");
        }
        sb.append("};\n");
        System.out.println(sb);
    }

    private static void sanityChecks() {
        System.out.println("King attack at h7");
        Util.printBitmap(KING_ATTACK[Util.squareToIndex("h7")]);
        System.out.println("White doubled pawn mask at c3");
        Util.printBitmap(WHITE_DOUBLED_PAWN_MASK[Util.squareToIndex("c3")]);
        System.out.println("Black doubled pawn mask at c3");
        Util.printBitmap(BLACK_DOUBLED_PAWN_MASK[Util.squareToIndex("c3")]);
        System.out.println("Isolated pawn mask at b2");
        Util.printBitmap(ISOLATED_PAWN_MASK[Util.squareToIndex("b2")]);
        System.out.println("Isolated pawn mask at h2");
        Util.printBitmap(ISOLATED_PAWN_MASK[Util.squareToIndex("h2")]);
        System.out.println("White passed pawn mask at g4");
        Util.printBitmap(WHITE_PASSED_PAWN_MASK[Util.squareToIndex("g4")]);
        System.out.println("White passed pawn mask at c7");
        Util.printBitmap(WHITE_PASSED_PAWN_MASK[Util.squareToIndex("c7")]);
        System.out.println("Black passed pawn mask at a5");
        Util.printBitmap(BLACK_PASSED_PAWN_MASK[Util.squareToIndex("a5")]);
    }

    public static void main(String[] args) {
        computePawnAttack(true);
        computePawnAttack(false);
        computeKnightAttack();
        computeKingAttack();
        computeDoubledPawnMasks();
        computeIsolatedPawnMasks();
        computePassedPawnMasks();
        sanityChecks();
    }
}
