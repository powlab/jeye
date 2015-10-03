package org.powlab.jeye.tests.types;

public class TypeTest21 {

    private int shortFn(short x) {
        return x;
    }

    private int charFn(char x) {
        return x;
    }

    public void test3(boolean b1) {
        short s = b1 ? (short) 1 : 0;
        char ch = b1 ? (char) 1 : 0;
        short s2 = (short) shortFn(s);
        int i1 = shortFn(s2);
        int i2 = charFn(ch);
    }
}
