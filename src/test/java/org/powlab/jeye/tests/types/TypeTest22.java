package org.powlab.jeye.tests.types;

public class TypeTest22 {

    private int i;

    private int shortFn(short x) {
        return x;
    }

    public void test3(short s2) {
        i = shortFn(s2);
    }
}
