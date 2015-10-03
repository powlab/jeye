package org.powlab.jeye.tests.boxing;

public class BoxingTest13 {
    private void t(Integer reference0, Integer reference1) {
        if (reference0 == reference1) {
            throw new IllegalStateException();
        }
        System.out.println("A");
    }

    private void t2(Integer reference0, Integer reference1) {
        if (reference0 == (int)reference1) {
            throw new IllegalStateException();
        }
        System.out.println("A");
    }

    private void t(int int0, Integer reference0) {
        if (int0 != reference0) {
            throw new IllegalStateException();
        }
        System.out.println("A");
    }

}
