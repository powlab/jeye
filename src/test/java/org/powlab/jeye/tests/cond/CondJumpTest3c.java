package org.powlab.jeye.tests.cond;

public class CondJumpTest3c {

    public boolean test(boolean a, boolean b) {
        boolean c = false;
        if ((b && a == (c = b) && b) || !c) {
            System.out.println(c);
        }
        return c;
    }
}
