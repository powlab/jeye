package org.powlab.jeye.tests.boxing;


public class BoxingTest29 {

    private void intFn(Integer reference0) {
    }

    private void t(Double reference0) {
        intFn((int)(double)reference0);
    }
}
