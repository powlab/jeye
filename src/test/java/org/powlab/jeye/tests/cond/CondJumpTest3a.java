package org.powlab.jeye.tests.cond;

public class CondJumpTest3a {

    public boolean test(boolean a, boolean b) {
        boolean c = false;
        boolean res = b && a == (c = b) && b && c;
        System.out.println(c);
        return res;
    }

}
