package org.powlab.jeye.tests.switches;

import org.powlab.jeye.tests.support.Troolean;

public class SwitchTest9 {

    public void test1(int a, int b) {
        switch (Troolean.get(a == 3, b == 12)) {
            case NEITHER:
                System.out.println("0");
            case FIRST:
                System.out.println("1");
                break;
            case BOTH:
                System.out.println("2");
            default:
        }
        System.out.println("Test");
    }

}
