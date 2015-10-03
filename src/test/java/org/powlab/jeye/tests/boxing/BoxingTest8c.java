package org.powlab.jeye.tests.boxing;

public class BoxingTest8c {

    private boolean t(Integer reference0, Integer reference1, int int0) {
        System.out.println(reference0);
        foo(reference1);
        foo(int0);
        return false;
    }

    private void foo(Object reference0) {
    }

    private void foo(int int0) {
    }

}
