package org.powlab.jeye.tests.cond;

public class CondJumpTest7 {

    public boolean test(int a, int b) {
        int c;
        return ++a == (c = b + a) && c < 0;
    }
}
