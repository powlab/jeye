package org.powlab.jeye.tests.other;

public class MultiClassTest1 {
    int x;
    int y;
    int z;

    int test1(int a, int b) {
        return a + b;
    }
}

class ExtraClass {
    int foo;

    public int testfoo(int x, int y) {
        return x + y * 2;
    }

    public int testbar(int a) {
        return a + 2;
    }
}
