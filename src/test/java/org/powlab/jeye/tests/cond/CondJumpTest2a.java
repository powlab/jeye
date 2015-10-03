package org.powlab.jeye.tests.cond;


public class CondJumpTest2a {


    public boolean test(boolean  a, boolean  b)
    {
        if (a) {
            System.out.println("t1");
            if (b) {
                return true;
            }
        }
        return false;
    }
}
