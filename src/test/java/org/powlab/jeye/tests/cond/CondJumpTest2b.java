package org.powlab.jeye.tests.cond;


public class CondJumpTest2b {


    public boolean test(boolean  a, boolean  b)
    {
        if (a) {
            System.out.println("t1");
            if (b) {
                return true;
            }
            System.out.println("t2");
        }
        return false;
    }
}
