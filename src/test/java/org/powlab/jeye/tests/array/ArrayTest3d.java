package org.powlab.jeye.tests.array;

public class ArrayTest3d {
    int x;
    int y;
    int z;

    void test1(int int0, int int1) {
        String[] array4;
        String[] array5 = (array4 = new String[] { "a", "b", "c" });
        int int4 = array5.length;
        for (int int5 = 0; int5 < int4; ++int5) {
            String reference0 = array5[int5];
            if (reference0 == null) {
                break;
            }
            System.out.println(reference0);
        }
        System.out.println("foo");
    }

}
