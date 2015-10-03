package org.powlab.jeye.tests.array;

public class ArrayTest4c {
    int x;
    int y;
    int z;

    void test1(int int0, int int1) {
        String[] array7 = new String[]{"a", "b", "c"};
        Object[] array9 = new String[]{"a", "b"};
        test2(array7);
        test2(array9);
        test2((Object[])array7);
        test2(new String[]{"a"});
        test2(new Object[]{"a"});
    }

    void test2(Object [] array0) {

    }

    void test2(String [] array0) {

    }
}
