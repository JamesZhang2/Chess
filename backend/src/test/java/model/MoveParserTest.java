package model;

import static org.junit.jupiter.api.Assertions.*;

class MoveParserTest {
    /*
     * Match:
     * e4
     * Kb1
     * Qe7
     * Rd1
     * Bb8
     * Nd2
     * Kxb1
     * Qxc3
     * Rxh8
     * Bxa1
     * Nxf2
     * axg7
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