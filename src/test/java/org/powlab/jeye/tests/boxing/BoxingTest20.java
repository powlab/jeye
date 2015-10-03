package org.powlab.jeye.tests.boxing;


public class BoxingTest20 {
    private void t(boolean boolean0) {
        Double reference2 = 4.3;
        Integer reference3 = 3;
        short short1 = (short)(boolean0 ? reference3 : (int)Integer.valueOf((int)reference2.doubleValue()));
    }

}
