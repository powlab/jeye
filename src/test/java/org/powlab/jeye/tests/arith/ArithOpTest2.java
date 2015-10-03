package org.powlab.jeye.tests.arith;

public class ArithOpTest2 {
    public void test1(int int0, Integer reference0, int int1) {
        System.out.println(int0 + reference0 * int1);
        System.out.println(int0 * (reference0 + int1));
        System.out.println(int0 * reference0 * int1);
    }


    public void test2(Integer reference0, int int0, Integer reference1) {
        System.out.println((reference0 + int0) * (reference1 + reference0) * (int0 + reference1 + reference0));
    }


    public void test3(int int0, Integer reference0, int int1) {
        System.out.println(int0 / reference0 * int1 + int1 * int0 * (reference0 * int1 + int0));
    }

    public void test4(Integer reference0, int int0, int int1) {
        System.out.println(reference0 / (int0 + int1));
        System.out.println(reference0 / int0 + int1);
        System.out.println(reference0 / int0 + int1);
    }
}
