package org.powlab.jeye.tests.boxing;

public class BoxingTest14 {
    private void test(Object reference0) {
    }

    private void test(int int0) {
    }


    private void t(int int0) {
        test(Integer.valueOf(int0));
        test(int0);
    }
}
