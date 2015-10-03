package org.powlab.jeye.tests.boxing;


public class BoxingTest19 {
    private void t(boolean boolean0) {
        Double reference1 = 4.3;
        int int1 = 2;
        short short1 = (short)(boolean0 ? int1 : reference1);
    }

}
