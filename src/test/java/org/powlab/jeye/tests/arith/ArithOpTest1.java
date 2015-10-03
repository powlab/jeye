package org.powlab.jeye.tests.arith;

public class ArithOpTest1 {
    public void test1(int int0, int int1, int int2) {
        System.out.println(int0 + int1 * int2);
        System.out.println(int0 * (int1 + int2));
        System.out.println(int0 * int1 * int2);
    }

    public void test2(int int0, int int1, int int2) {
        System.out.println((int0 + int1) * (int2 + int0) * (int1 + int2 + int0));
    }

    public void test3(int int0, int int1, int int2) {
        System.out.println(int0 / int1 * int2 + int2 * int0 * (int1 * int2 + int0));
    }

    public void test4(int int0, int int1, int int2) {
        System.out.println(int0 / (int1 + int2));
        System.out.println(int0 / int1 + int2);
        System.out.println(int0 / int1 + int2);
    }
}
