package org.powlab.jeye.tests.boxing;

public class BoxingTest16b {
    private void test(Object o) {
//        System.out.println("Object");
    }

    private void test(Number i) {
//        System.out.println("Integer");
    }

    private void test(int i) {
//        System.out.println("int");
    }


    private void t(int x) {
        test(null);
    }

}
