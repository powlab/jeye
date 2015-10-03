package org.powlab.jeye.tests.loop;


public class LoopTest13 {

    public void test5(int end) {
        for (int x = 0; (x = x + 2) < end; ) {
            System.out.println(x);
        }
    }

}
