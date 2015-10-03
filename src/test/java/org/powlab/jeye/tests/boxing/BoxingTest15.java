package org.powlab.jeye.tests.boxing;

public class BoxingTest15 {
    private void test(Object reference0) {
        System.out.println("Object");
    }

    private void test(Integer reference0) {
        System.out.println("Integer");
    }

    private void test(int int0) {
        System.out.println("int");
    }

    private void t(int int0) {
        test(Integer.valueOf(int0));
        test((Object) int0);
        test(int0);
    }

}
