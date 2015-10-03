package org.powlab.jeye.tests.boxing;

public class BoxingTest16 {
    private void test(Object o) {
//        System.out.println("Object");
    }

    private void test(Integer i) {
//        System.out.println("Integer");
    }

    private void test(int i) {
//        System.out.println("int");
    }


    private void t(int x) {
        test(null);
        test((Integer)null);
        test((Number)null);
        test((Object)null);
    }

}
