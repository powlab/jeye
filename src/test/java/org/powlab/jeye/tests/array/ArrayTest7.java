package org.powlab.jeye.tests.array;

import java.util.List;

public class ArrayTest7 {
    int x;
    int y;
    int z;

    void test3(int int0, int int1) {
        // This information is COMPLETELY lost. :(
        List<Integer>[] array1 = (List<Integer>[]) new List<?>[10];
    }

}
