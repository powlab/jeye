package org.powlab.jeye.tests.boxing;

import java.util.List;

public class BoxingTest11 {
    private void t(Integer reference0, List<Integer> reference1) {
        for (Integer reference5 : reference1) {
            if (reference5 == reference0) {
                throw new IllegalStateException();
            }
            System.out.println("A");
        }
    }

    private void t(int int0, List<Integer> reference0) {
        for (Integer reference4 : reference0) {
            if (reference4 == int0) {
                throw new IllegalStateException();
            }
            System.out.println("A");
        }
    }

}
