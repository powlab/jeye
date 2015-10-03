package org.powlab.jeye.tests.boxing;


public class BoxingTest25 {
    private void t(boolean b) {
        Double reference2 = 4.3;
        Integer reference3 = null;
        fn((int)reference2.doubleValue());
        fn(reference3.intValue());
    }

    private void fn(Integer i) {
    }

}
