package org.powlab.jeye.tests.boxing;


public class BoxingTest36 {
    private void t(boolean boolean0) {
        Integer reference1 = new Integer(12);
        fn(reference1, (int)reference1);
    }

    private boolean fn(Integer reference0, Integer reference1) {
        return reference1 == reference0;
    }

}
