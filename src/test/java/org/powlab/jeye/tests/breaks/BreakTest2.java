package org.powlab.jeye.tests.breaks;

import java.util.List;


public class BreakTest2 {

    boolean testA(int int0, int int1) {
        return int0<int1;
    }

    boolean testB(int int0, int int1) {
        return int0<int1;
    }

    boolean testC(int int0, int int1) {
        return int0<int1;
    }

    public int doIt(List<Integer> reference0, List<Integer> reference1) {
        labelA: for (Integer reference6 : reference0) {
            labelB: for (Integer reference9 : reference1) {
                System.out.println("A");
                if (reference9 == reference6)
                    break labelA;
                System.out.println("B");
                if (reference9 > reference6)
                    break labelB;
                System.out.println("C");
            }
        }
        return 1;
    }
}
