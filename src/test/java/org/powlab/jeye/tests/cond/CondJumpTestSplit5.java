package org.powlab.jeye.tests.cond;


public class CondJumpTestSplit5 {


    public boolean test(boolean a, boolean b) {
        int c;
        if (a) {
            System.out.println("true");
            c = 1;
        } else {
            System.out.println("false");
            c = 0;
        }
        return b;
    }


}
