package org.powlab.jeye.tests.loop;


public class LoopTest20 {

    int y;

    public void test1(int end) {
        for (int y = 0; y < end; ++y) {
            System.out.println(y);
        }
    }

    public void test2(int end) {
        for (y = 0; y < end; ++y) {
            System.out.println(y);
        }
    }
}
