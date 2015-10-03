package org.powlab.jeye.tests.other;


public class EllipsisTest1 {

    void test1(String a, String b, String c) {
        test2(a, b, c);
        test2(new String[]{a, b, c});
        test3(new String[]{a, b, c});
    }

    void test2(String... a) {
        for (String x : a) {
            System.out.println(x);
        }
    }

    void test3(String[] a) {
        for (String x : a) {
            System.out.println(x);
        }
    }
}
