package org.powlab.jeye.tests.array;

public class ArrayTest10a {

    private static class CA {
    }

    private static class CB extends CA {
    }

    private static class CC extends CA {
    }

    void test3() {
        CA[] a;
        a = new CB[4];
        a = new CC[4];
        System.out.println(a);
        System.out.println(a);
    }

}
