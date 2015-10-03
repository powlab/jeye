package org.powlab.jeye.tests.boxing;


public class BoxingTest26 {
    private void t(boolean boolean0) {
        Integer reference2 = 3;
        Integer reference3 = 3;
        int int1 = 3;
        fn(reference2.intValue());
        fn(reference3);
        fn(int1);
        fn(Integer.valueOf(int1));
    }

    private void fn(Integer reference0) {
    }

    private void fn(int int0) {
    }

}
