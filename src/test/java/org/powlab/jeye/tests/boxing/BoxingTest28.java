package org.powlab.jeye.tests.boxing;


public class BoxingTest28 {
    private void test(Object reference0) {
    }

    private void test(Short reference0) {
    }

    private void test(Integer reference0) {
    }

    private void test(int int0) {
    }

    private void t(int int0) {
//        test(Integer.valueOf(int0));
//        test(Short.valueOf((short)int0));
//        test((short)int0);
        test((Object)int0);//крутяк
//        test(int0);
    }

}