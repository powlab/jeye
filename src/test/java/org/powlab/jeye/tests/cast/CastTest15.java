package org.powlab.jeye.tests.cast;

public class CastTest15 {

    static byte a = 1;

    public static void foo() {
        byte byte1 = (byte)(a^1);
        char char1 = (char)a;
    }
}
