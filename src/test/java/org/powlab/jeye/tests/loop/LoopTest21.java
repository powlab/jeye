package org.powlab.jeye.tests.loop;


public class LoopTest21 {

    Integer y;

    public void test1(Integer end) {
        for (Integer y = 0; y < end; ++y) {
            System.out.println(y);
        }
    }

    public void test2(Integer end) {
        if (Integer.valueOf(y) == null) return;
        for (y = 0; y < end; ++y) {
            System.out.println(y);
        }
    }
}
