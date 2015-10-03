package org.powlab.jeye.tests.array;

public class ArrayTest9 {
    int x;
    int y;
    int z;

    static void test1(Integer... array0) {
        System.out.println(array0);
    }

    public static void main(String[] array0) {
        test1(1, 2, 3);
        test1(1);
        test1((Integer[]) null);
        test1();
    }

}
