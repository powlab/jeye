package org.powlab.jeye.tests.boxing;


public class BoxingTest30 {

    private void intFn(Integer i) {
    }

    private void t(Double d) {
        Integer i1 = (int)(double)d;
    }
}
