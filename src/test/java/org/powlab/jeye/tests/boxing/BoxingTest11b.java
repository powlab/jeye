package org.powlab.jeye.tests.boxing;

import java.util.Iterator;
import java.util.List;

public class BoxingTest11b {


    private void t(int int0, List<Integer> reference0) {
        System.out.println("here");
        Iterator<Integer> reference3 = reference0.iterator();
        while (true) {
            if (!reference3.hasNext()) {
                return;
            }
            Integer reference4 = reference3.next();
            if (reference4 == int0) {
                throw new IllegalStateException();
            }
            System.out.println("A");
        }
    }

}
