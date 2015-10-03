package org.powlab.jeye.tests.cond;

public class CondJumpTestInlineAssign {

    boolean thisa;

    public boolean test(boolean a, boolean b) {
        return a || b == (thisa = a);
    }

}
