package org.powlab.jeye.tests.types;

public class TypeTest24 {
    private short s;
    private int i;
    private long l;
    private byte b;
    private boolean bl;

    public void test3(boolean a) {
        b = (byte) (a ? 1 : 0);
        i = a ? 1 : 0;
        l = (long) (a ? 1 : 0);
        s = (short) (a ? 1 : 0);
        bl = a;
    }
}
