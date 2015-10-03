package org.powlab.jeye.tests.types;

public class TypeTest23 {
    private short s;
    private int i;
    private long l;
    private byte b;
    private boolean bl;

    public void test3(int a) {
        b = (byte)a;
        i = a;
        l = (long)a;
        s = (short)a;
        bl = a == 1;
    }
}
