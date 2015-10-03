package org.powlab.jeye.tests.array;

public class ArrayTest10c {

    private static class CA {
    }

    private static class CB extends CA {
    }

    private static class CC extends CA {
    }

    void test3(boolean x) {
        CA[] a;
        if (x) {
            a = new CB[4];
        } else {
            a = new CC[4];
        }
        System.out.println(a);
    }

}
