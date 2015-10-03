package org.powlab.jeye.tests.boxing;


public class BoxingTest28b {
    private void test(Object reference0) {
    }

    private void test(Short reference0) {
    }

    private void test(Integer reference0) {
    }

    private void test(int int0) {
    }


    private void t(int int0) {
        test(Short.valueOf((short)int0));
    }
}
