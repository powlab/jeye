package org.powlab.jeye.tests.array;

public class ArrayTest10b {

    private static class CA {
    }

    private static class CB extends CA {
    }

    private static class CC extends CA {
    }

    void test3(boolean x) {
        CA[] a;
        a = x ? new CB[4] : new CC[4];
    }

}
