package org.powlab.jeye.tests.boxing;

import java.util.List;

public class BoxingTest11a {


    private void t(int int0, List<Integer> reference0) {
        for (Integer reference4 : reference0) {
            if (reference4 == int0) {
                throw new IllegalStateException();
            }
            reference4++;
            System.out.println("A");
        }
    }

}
