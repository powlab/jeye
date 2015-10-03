package org.powlab.jeye.tests.cond;

public class CondJumpTest3 {

    public boolean test(boolean a, boolean b) {
        boolean c;
        return b && a == (c = b) && b && c;
    }

}
