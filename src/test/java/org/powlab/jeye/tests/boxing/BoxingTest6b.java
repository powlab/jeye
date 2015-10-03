package org.powlab.jeye.tests.boxing;

public class BoxingTest6b {
    // Байкод генерится один и тот же для 1 и 2 строк метода, в чем разница?
    private void IntegerF(Integer reference0) {
        DoubleF((double)reference0);
        //DoubleF(Double.valueOf(reference0.intValue()));
    }

    private void DoubleF(Double reference0) {
    }

}
