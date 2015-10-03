package org.powlab.jeye.tests.primitives;

public class StrictFPTest2 {

    public double test1(double a, double b) {
        return a+b;
    }

    public strictfp double test2(double a, double b) {
        return a+b;
    }
}
