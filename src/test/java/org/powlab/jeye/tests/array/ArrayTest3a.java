package org.powlab.jeye.tests.array;

public class ArrayTest3a {
    int x;
    int y;
    int z;

    void test1(int int0, int int1) {
        String[] array4;
        String[] array5 = (array4 = new String[]{"a", "b", "c"});
        int int2 = array5.length;
        int int3 = 0;
        do {
            if (int3 >= int2) {
                return;
            }
            String s = array5[int3];
            System.out.println(s);
            ++int3;
        } while (true);
    }

}
