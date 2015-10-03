package org.powlab.jeye.tests.boxing;

public class BoxingTest12 {
    private void t(Integer reference0, Integer[] array0) {
        for (Integer reference2 : array0) {
            if (reference2 == reference0) {
                throw new IllegalStateException();
            }
            System.out.println("A");
        }
    }

    private void t(int int0, Integer[] array0) {
        for (Integer reference1 : array0) {
            if (reference1 == int0) {
                throw new IllegalStateException();
            }
            System.out.println("A");
        }
    }

}
