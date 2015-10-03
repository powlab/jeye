package org.powlab.jeye.tests.boxing;

public class BoxingTest6 {
    private void IntegerF(Integer reference0) {
        IntegerF(reference0);
        intF(reference0);
        doubleF(reference0);
        DoubleF((double) reference0);
        ShortF((short) reference0.intValue());
        shortF((short) reference0.intValue());
    }

    private void intF(int int0) {
        IntegerF(int0);
        intF(int0);
        doubleF(int0);
        DoubleF((double) int0);
        ShortF((short) int0);
        shortF((short) int0);
    }

    private void doubleF(double double0) {
        IntegerF((int) double0);
        intF((int) double0);
        doubleF(double0);
        DoubleF(double0);
        ShortF((short) double0);
        shortF((short) double0);
    }

    private void DoubleF(Double reference0) {
        IntegerF((int) reference0.doubleValue());
        intF((int) reference0.doubleValue());
        doubleF(reference0);
        DoubleF(reference0);
        ShortF((short) reference0.doubleValue());
        shortF((short) reference0.doubleValue());
    }

    private void ShortF(Short reference0) {
        IntegerF((int) reference0);
        intF(reference0);
        doubleF(reference0);
        DoubleF((double) reference0);
        ShortF(reference0);
        shortF(reference0);
    }

    private void shortF(short short0) {
        IntegerF((int) short0);
        intF(short0);
        doubleF(short0);
        DoubleF((double) short0);
        ShortF(short0);
        shortF(short0);
    }




}
