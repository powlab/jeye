package org.powlab.jeye.tests.boxing;


public class BoxingTest29b {

    private void intFn(Integer reference0) {
    }

    private void t(double double0) {
        intFn((Integer)(int)double0);
    }
}
