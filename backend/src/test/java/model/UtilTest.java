package model;

import model.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void testSetBit() {
        assertEquals(1L, Util.setBit(0, 0, 0));
        assertEquals(0x0000000004000000L, Util.setBit(0, 3, 2));
        assertEquals(0x8000000000000000L, Util.setBit(0, 7, 7));
        assertEquals(0x1000000000000000L, Util.setBit(0x1000000000000000L, 7, 4));
        assertEquals(0xF000000000000000L, Util.setBit(0xF000000000000000L, 7, 7));
        assertEquals(0xF000000000000001L, Util.setBit(0xF000000000000000L, 0, 0));
        assertEquals(0x3141592653589793L, Util.setBit(0x3141592653589791L, 0, 1));
        assertEquals(0x3141592653589793L, Util.setBit(0x3141592653589793L, 0, 1));
        assertEquals(1L, Util.setBit(0, 0));
        assertEquals(0x0000000004000000L, Util.setBit(0, 26));
        assertEquals(0x8000000000000000L, Util.setBit(0, 63));
        assertEquals(0x1000000000000000L, Util.setBit(0x1000000000000000L, 60));
        assertEquals(0xF000000000000000L, Util.setBit(0xF000000000000000L, 63));
        assertEquals(0xF000000000000001L, Util.setBit(0xF000000000000000L, 0));
        assertEquals(0x3141592653589793L, Util.setBit(0x3141592653589791L, 1));
        assertEquals(0x3141592653589793L, Util.setBit(0x3141592653589793L, 1));
    }

    @Test
    void testClearBit() {
        assertEquals(0L, Util.clearBit(0L, 4, 2));
        assertEquals(0L, Util.clearBit(1L, 0, 0));
        assertEquals(0L, Util.clearBit(0x80L, 0, 7));
        assertEquals(0L, Util.clearBit(0x8000000000000000L, 7, 7));
        assertEquals(0xB000000000000000L, Util.clearBit(0xF000000000000000L, 7, 6));
        assertEquals(0xFF00000000000000L, Util.clearBit(0xFF00000000000000L, 6, 7));
        assertEquals(0x3141592653589791L, Util.clearBit(0x3141592653589793L, 0, 1));
        assertEquals(0x3141592653589793L, Util.clearBit(0x3141592653589793L, 0, 2));
        assertEquals(0L, Util.clearBit(0L, 34));
        assertEquals(0L, Util.clearBit(1L, 0));
        assertEquals(0L, Util.clearBit(0x80L, 7));
        assertEquals(0L, Util.clearBit(0x8000000000000000L, 63));
        assertEquals(0xB000000000000000L, Util.clearBit(0xF000000000000000L, 62));
        assertEquals(0xFF00000000000000L, Util.clearBit(0xFF00000000000000L, 55));
        assertEquals(0x3141592653589791L, Util.clearBit(0x3141592653589793L, 1));
        assertEquals(0x3141592653589793L, Util.clearBit(0x3141592653589793L, 2));
    }

    @Test
    void testGetBit() {
        assert !Util.getBit(0L, 0, 0);
        assert Util.getBit(0x1L, 0, 0);
        assert Util.getBit(0x1000000000000000L, 7, 4);
        assert !Util.getBit(0x1000000000000000L, 7, 7);
        assert Util.getBit(0x8000000000000000L, 7, 7);
        assert !Util.getBit(0x8000000000000000L, 7, 6);
        assert !Util.getBit(0x8000000000000000L, 0, 0);
        assert Util.getBit(0x3141592653589793L, 0, 0);
        assert !Util.getBit(0x3141592653589793L, 0, 2);
        assert !Util.getBit(0L, 0);
        assert Util.getBit(0x1L, 0);
        assert Util.getBit(0x1000000000000000L, 60);
        assert !Util.getBit(0x1000000000000000L, 63);
        assert Util.getBit(0x8000000000000000L, 63);
        assert !Util.getBit(0x8000000000000000L, 62);
        assert !Util.getBit(0x8000000000000000L, 0);
        assert Util.getBit(0x3141592653589793L, 0);
        assert !Util.getBit(0x3141592653589793L, 2);
    }

    @Test
    void testSetClearGetBit() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            long bitmap = random.nextLong();
            int row = random.nextInt(8);
            int col = random.nextInt(8);
            assert !Util.getBit(Util.clearBit(bitmap, row, col), row, col);
            assert Util.getBit(Util.setBit(bitmap, row, col), row, col);
        }
        for (int i = 0; i < 1000; i++) {
            long bitmap = random.nextLong();
            int idx = random.nextInt(64);
            assert !Util.getBit(Util.clearBit(bitmap, idx), idx);
            assert Util.getBit(Util.setBit(bitmap, idx), idx);
        }
    }

    private long naiveIsolateLS1B(long bitmap) {
        assert bitmap != 0;
        for (int i = 0; i < 64; i++) {
            if (((bitmap >>> i) & 1) == 1) {
                return 1L << i;
            }
        }
        assert false;
        return 0;
    }

    @Test
    void testIsolateLS1B() {
        // Edge cases
        assertEquals(1L, Util.isolateLS1B(1L));
        assertEquals(1L, Util.isolateLS1B(0xFFFFFFFFFFFFFFFFL));
        assertEquals(1L, Util.isolateLS1B(0x8000000000000001L));
        assertEquals(0x8000000000000000L, Util.isolateLS1B(0x8000000000000000L));
        assertEquals(0x1000000000000000L, Util.isolateLS1B(0xF000000000000000L));
        assertEquals(1L, Util.isolateLS1B(0x3141592653589793L));
        // Randomized
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            long bitmap = random.nextLong();
            while (bitmap == 0) {
                // This basically never happens: 1 in 2^64 chance!
                bitmap = random.nextLong();
            }
            assertEquals(naiveIsolateLS1B(bitmap), Util.isolateLS1B(bitmap));
        }
    }

    private long naiveGetLS1BIdx(long bitmap) {
        assert bitmap != 0;
        for (int i = 0; i < 64; i++) {
            if (((bitmap >>> i) & 1) == 1) {
                return i;
            }
        }
        assert false;
        return 0;
    }

    @Test
    void testGetLS1BIdx() {
        // Edge cases
        assertEquals(0, Util.getLS1BIdx(1L));
        assertEquals(0, Util.getLS1BIdx(0xFFFFFFFFFFFFFFFFL));
        assertEquals(0, Util.getLS1BIdx(0x8000000000000001L));
        assertEquals(63, Util.getLS1BIdx(0x8000000000000000L));
        assertEquals(60, Util.getLS1BIdx(0xF000000000000000L));
        assertEquals(0, Util.getLS1BIdx(0x3141592653589793L));
        // Randomized
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            long bitmap = random.nextLong();
            while (bitmap == 0) {
                bitmap = random.nextLong();
            }
            assertEquals(naiveGetLS1BIdx(bitmap), Util.getLS1BIdx(bitmap));
        }
    }

    private long naiveResetLS1B(long bitmap) {
        assert bitmap != 0;
        for (int i = 0; i < 64; i++) {
            if (((bitmap >>> i) & 1) == 1) {
                return bitmap & ~(1L << i);
            }
        }
        assert false;
        return 0;
    }

    @Test
    void testResetLS1B() {
        // Edge cases
        assertEquals(0, Util.resetLS1B(1L));
        assertEquals(0xFFFFFFFFFFFFFFFEL, Util.resetLS1B(0xFFFFFFFFFFFFFFFFL));
        assertEquals(0x8000000000000000L, Util.resetLS1B(0x8000000000000001L));
        assertEquals(0, Util.resetLS1B(0x8000000000000000L));
        assertEquals(0xE000000000000000L, Util.resetLS1B(0xF000000000000000L));
        assertEquals(0x3141592653589792L, Util.resetLS1B(0x3141592653589793L));
        // Randomized
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            long bitmap = random.nextLong();
            while (bitmap == 0) {
                bitmap = random.nextLong();
            }
            assertEquals(naiveResetLS1B(bitmap), Util.resetLS1B(bitmap));
        }
    }

    private long naivePopCount(long bitmap) {
        int count = 0;
        for (int i = 0; i < 64; i++) {
            if (((bitmap >>> i) & 1) == 1) {
                count++;
            }
        }
        return count;
    }

    @Test
    void testPopCount() {
        // Edge cases
        assertEquals(0, Util.popCount(0L));
        assertEquals(1, Util.popCount(1L));
        assertEquals(64, Util.popCount(0xFFFFFFFFFFFFFFFFL));
        assertEquals(2, Util.popCount(0x8000000000000001L));
        assertEquals(1, Util.popCount(0x8000000000000000L));
        assertEquals(4, Util.popCount(0xF000000000000000L));
        assertEquals(28, Util.popCount(0x3141592653589793L));
        // Randomized
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            long bitmap = random.nextLong();
            assertEquals(naivePopCount(bitmap), Util.popCount(bitmap));
        }
    }

    @Test
    void testBitmapToString() {
        assertEquals("........\n........\n........\n........\n........\n........\n........\n........\n",
                Util.bitmapToString(0L));
        assertEquals("........\n........\n........\n........\n........\n........\n........\n1.......\n",
                Util.bitmapToString(1L));
        assertEquals("........\n........\n........\n........\n........\n........\n........\n.......1\n",
                Util.bitmapToString(0x80L));
        assertEquals("........\n........\n........\n........\n........\n........\n........\n11111111\n",
                Util.bitmapToString(Util.RANK_1));
        assertEquals("11111111\n........\n........\n........\n........\n........\n........\n........\n",
                Util.bitmapToString(Util.RANK_8));
        assertEquals("1.......\n1.......\n1.......\n1.......\n1.......\n1.......\n1.......\n1.......\n",
                Util.bitmapToString(Util.A_FILE));
        assertEquals(".......1\n.......1\n.......1\n.......1\n.......1\n.......1\n.......1\n.......1\n",
                Util.bitmapToString(Util.H_FILE));
        assertEquals("1...11..\n1.....1.\n1..11.1.\n.11..1..\n11..1.1.\n...11.1.\n111.1..1\n11..1..1\n",
                Util.bitmapToString(0x3141592653589793L));
        System.out.println("Rank 1:");
        Util.printBitmap(Util.RANK_1);
        System.out.println("Rank 8:");
        Util.printBitmap(Util.RANK_8);
        System.out.println("a file:");
        Util.printBitmap(Util.A_FILE);
        System.out.println("h file:");
        Util.printBitmap(Util.H_FILE);
    }
}
