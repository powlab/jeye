package org.powlab.jeye.tests.loop;


public class LoopTest10 {

    char[] foo;

    public void test(int end) {
        int c = 0;
        while (++c < end || ++c < end) {
            System.out.println(":" + c);
        }
    }

    public void test2(int end) {
        int c = 0;
        while (++c < end || c++ < end) {
            System.out.println(":" + ++c);
        }
    }

    public void test3(int end) {
        int c = 0;
        while (c++ < end || c++ < end) {
            System.out.println(":" + c++);
        }
    }
}
