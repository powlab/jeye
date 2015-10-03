package org.powlab.jeye.tests.cond;

public class CondJumpTest2c {

    public boolean test(boolean a, boolean b) {
        boolean c;
        return (b && a == (c = b)) || (b && (c = a));
    }
}
