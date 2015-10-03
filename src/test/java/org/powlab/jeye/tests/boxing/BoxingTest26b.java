package org.powlab.jeye.tests.boxing;


public class BoxingTest26b {
    private void t(boolean boolean0) {
        int int0 = 3;
        // байт код такой же как и для fn(int0)
        fn(Integer.valueOf(int0));
    }

    private void fn(Integer reference0) {
    }


}
