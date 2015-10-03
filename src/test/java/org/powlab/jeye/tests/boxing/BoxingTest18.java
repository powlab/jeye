package org.powlab.jeye.tests.boxing;


public class BoxingTest18 {
    private void t(Integer[] array0) {
        for (Integer reference1 : array0) {
            reference1 = array0[reference1];
        }
    }

}
