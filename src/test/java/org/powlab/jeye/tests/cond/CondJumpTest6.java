package org.powlab.jeye.tests.cond;

public class CondJumpTest6 {

    public boolean test(int a, int b) {
        int c = a;
        return c > 0 && a == (c = b + a) && c < 0;
    }
}
