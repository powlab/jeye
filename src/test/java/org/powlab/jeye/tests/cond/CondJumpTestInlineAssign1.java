package org.powlab.jeye.tests.cond;

public class CondJumpTestInlineAssign1 {

    boolean thisa;

    public boolean test(boolean a, boolean b) {
        return b == (thisa = a);
    }

}