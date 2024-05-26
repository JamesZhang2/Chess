package model;

/**
 * A class for global constants and utilities.
 */
public class Util {
    public static final String START_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static final char[] PIECE_NAMES = {'p', 'n', 'b', 'r', 'q', 'k', 'P', 'N', 'B', 'R', 'Q', 'K'};
    public static final char[] WHITE_PIECE_NAMES = {'P', 'N', 'B', 'R', 'Q', 'K'};
    public static final char[] BLACK_PIECE_NAMES = {'p', 'n', 'b', 'r', 'q', 'k'};

    public static final int[] DE_BRUIJN_LOOKUP = {
            0, 1, 48, 2, 57, 49, 28, 3,
            61, 58, 50, 42, 38, 29, 17, 4,
            62, 55, 59, 36, 53, 51, 43, 22,
            45, 39, 33, 30, 24, 18, 12, 5,
            63, 47, 56, 27, 60, 41, 37, 16,
            54, 35, 52, 21, 44, 32, 23, 11,
            46, 26, 40, 15, 34, 20, 31, 10,
            25, 14, 19, 9, 13, 8, 7, 6
    };
    public static final long DE_BRUIJN_SEQ = 0x03f79d71b4cb0a89L;

    public static final long RANK_1 = 0xFFL;
    public static final long RANK_8 = 0xFFL << 56;
    public static final long A_FILE = 0x0101010101010101L;
    public static final long H_FILE = 0x8080808080808080L;


    /**
     * @return true if input is in [0...7], false otherwise
     */
    public static boolean inRange(int input) {
        return input >= 0 && input <= 7;
    }

    /**
     * Returns the upper case of c.
     * Requires: c is either a lower-case or upper-case letter
     */
    public static char toUpperCase(char c) {
        return c >= 'a' ? (char) (c - 'a' + 'A') : c;
    }

    /**
     * Return the bitmap where we set (row, col) to 1 and leave the other bits unchanged
     * Requires: row and col are in [0...7]
     */
    public static long setBit(long original, int row, int col) {
        return original | (1L << (row * 8 + col));
    }

    /**
     * Return the bitmap where we set idx to 1 and leave the other bits unchanged
     * Requires: idx is in [0...63]
     */
    public static long setBit(long original, int idx) {
        return original | (1L << idx);
    }

    /**
     * Return the bitmap where we set (row, col) to 0 and leave the other bits unchanged
     * Requires: row and col are in [0...7]
     */
    public static long clearBit(long original, int row, int col) {
        return original & ~(1L << (row * 8 + col));
    }

    /**
     * Return the bitmap where we set idx to 0 and leave the other bits unchanged
     * Requires: idx is in [0...63]
     */
    public static long clearBit(long original, int idx) {
        return original & ~(1L << idx);
    }

    /**
     * Returns true if the bit at index idx of bimap is 1, false otherwise
     * Requires: idx is in [0...63]
     */
    public static boolean getBit(long bitmap, int idx) {
        return ((bitmap >>> idx) & 1) == 1;
    }

    /**
     * Returns true if the bit at {row, col} of bimap is 1, false otherwise
     * Requires: row, col are in [0...7]
     */
    public static boolean getBit(long bitmap, int row, int col) {
        return ((bitmap >>> (row * 8 + col)) & 1) == 1;
    }

    /**
     * Get the index of the least significant 1 bit.
     * Requires: bitmap is not 0
     *
     * @return the index of the least significant 1 bit
     */
    public static int getLS1BIdx(long bitmap) {
        // We use De Bruijn Multiplication. This works as follows: A 64-bit De Bruijn sequence has
        // different values in its most significant 6 bits with different left shifts (0 to 63).
        // So, we can isolate the LS1B, multiply it by the De Bruijn sequence (equivalent to left shifting)
        // then read from a lookup table to see how much it has been left shifted.
        // The algorithm and the De Bruijn sequence are obtained from https://www.chessprogramming.org/BitScan.
        long isolatedLS1B = isolateLS1B(bitmap);
        return DE_BRUIJN_LOOKUP[(int) ((DE_BRUIJN_SEQ * isolatedLS1B) >>> 58)];
    }

    /**
     * Isolate the least significant 1 bit.
     * Requires: bitmap is not 0
     *
     * @return the power of two corresponding to the least significant 1 bit
     */
    public static long isolateLS1B(long bitmap) {
        assert bitmap != 0;
        // https://www.chessprogramming.org/General_Setwise_Operations#LS1BIsolation
        return bitmap & (-bitmap);
    }

    /**
     * Reset the least significant 1 bit.
     * Requires: bitmap is not 0
     *
     * @return the bitmap after the least significant 1 bit is removed.
     */
    public static long resetLS1B(long bitmap) {
        assert bitmap != 0;
        // https://www.chessprogramming.org/General_Setwise_Operations#Reset
        return bitmap & (bitmap - 1);
    }

    /**
     * Population count.
     *
     * @return the number of 1s in bitmap
     */
    public static int popCount(long bitmap) {
        // https://www.chessprogramming.org/Population_Count
        // Using the Brian Kernighan's way: Repeatedly reset LS1B until bitmap becomes 0
        // Note that in Java, passing a long as a parameter creates a copy, so we don't have to worry about
        // modifying the original bitmap.
        int count = 0;
        while (bitmap != 0) {
            bitmap = resetLS1B(bitmap);
            count++;
        }
        return count;
    }

    /**
     * Turn the bitmap into a human-readable string, respecting the little-endian mapping
     * from bitmaps to boards
     *
     * @return an 8x8 grid of 1s and 0s
     */
    public static String bitmapToString(long bitmap) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            StringBuilder sb2 = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                sb2.append(bitmap & 1);
                bitmap >>>= 1;
            }
            sb2.append('\n');
            sb.insert(0, sb2.toString());
        }
        return sb.toString();
    }

    /**
     * Pretty-print bitmap
     */
    public static void printBitmap(long bitmap) {
        System.out.println(bitmapToString(bitmap));
    }
}
