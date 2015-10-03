package org.powlab.jeye.tests.breaks;

import java.util.List;


public class BreakTest5 {

    public int doIt(List<Integer> reference0, List<Integer> reference1) {
        int int1 = 1;
        while(true) {
            if (int1 > 200) {
                break;
            }
            System.out.println(int1);
            if ((int1 % 5) == 0) {
                ++int1;
            } else {
                int1+=5;
            }
        }
        return 1;
    }
}
