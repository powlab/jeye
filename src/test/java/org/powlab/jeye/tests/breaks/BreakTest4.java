package org.powlab.jeye.tests.breaks;

import java.util.List;


public class BreakTest4 {

    public int doIt(List<Integer> reference0, List<Integer> reference1) {
        for (int int1 : reference0) {
            System.out.println(reference1.contains(int1));
        }
        return 1;
    }
}
