package org.powlab.jeye.tests.array;

public class ArrayTest4a {
    int x;
    int y;
    int z;

    void test1(int int0, int int1) {
        String[] array3 = new String[]{"int0", "int1", "c"};
    }

    void test2(int int0, int int1) {
        Integer[] array3 = new Integer[]{int0, int1, x, y};
    }

    void test3(int int0, int int1) {
        Integer[][] array9 = new Integer[][]{{int0, int1}, {x, y}, {z}};
    }

    void test4(int int0, int int1) {
        Integer[][] array1 = new Integer[3][];
    }
}
