package org.powlab.jeye.tests.array;

public class ArrayTest3c {
    int x;
    int y;
    int z;

    void test1(int int0, int int1) {
        String[] array4;
        String[] array5 = (array4 = new String[]{"a", "b", "c"});
        int int4 = array5.length;
        int int5 = 0;
        do {
            if (int5 >= int4) {
                return;
            }
            String reference1 = array5[int5];
            System.out.println(reference1);
            if (reference1 == null) {
                break;
            }
            ++int5;
        } while (true);
        System.out.println("fred");
    }

}
