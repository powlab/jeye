package org.powlab.jeye.tests.cond;


public class CondJumpTestSplit3 {


    public boolean test(boolean a, boolean b) {
        boolean c;
        if (a) {
            System.out.println("true");
            c = true;
        } else {
            System.out.println("false");
            c = false;
        }
        return c;
    }


}
