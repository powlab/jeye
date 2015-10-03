package org.powlab.jeye.tests.boxing;

public class BoxingTest16a3 {
    private void test(Object reference0) {
//        System.out.println("Object");
    }

    private void test(Integer reference0) {
//        System.out.println("Integer");
    }

    private void test(Number reference0) {
//        System.out.println("Integer");
    }

    private void test(int int0) {
//        System.out.println("int");
    }


    private void t(int int0) {
        Integer reference1 = int0;
        test(reference1);
        test((Object)reference1);
        test((Number)reference1);
    }

}
