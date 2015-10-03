package org.powlab.jeye.tests.cond;

public class CondJumpTest1e {

    public boolean test(int a, int b, int c) {
        return (c + 2) > a || ((b - 2) < c && c > b) || (b - 3) < c;
    }

}
