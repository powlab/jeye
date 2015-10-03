package org.powlab.jeye.tests.cond;

public class CondJumpTest5 {

    public boolean test(boolean a, boolean b) {
        boolean c = a;
        return c && a == (c = b) && c;
    }
}
