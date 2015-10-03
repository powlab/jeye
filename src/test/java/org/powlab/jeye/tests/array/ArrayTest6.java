package org.powlab.jeye.tests.array;

public class ArrayTest6 {
    int x;
    int y;
    int z;

    void test3(int int0, int int1) {
        int[][] array1 = new int[3][3];
        array1[1][2] = 4;
    }

    void test3a(int int0, int int1) {
        Integer[][] array1 = new Integer[3][3];
        array1[1][2] = 4;
    }

    void test4() {
        int[] array1 = new int[4];
        array1[3] = 4;
    }
}
