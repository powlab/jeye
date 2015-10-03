package org.powlab.jeye.tests.boxing;


public class BoxingTest22 {
    private void t(boolean boolean0) {
        int int1 = 4;
        Integer reference1 = 3;
        short short1 = (short)(boolean0 ? reference1 : int1);
    }

}
