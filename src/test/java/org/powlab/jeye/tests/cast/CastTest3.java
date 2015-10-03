package org.powlab.jeye.tests.cast;



public class CastTest3 {

    public static void test1(long long0) {
        System.out.println(long0);
        long0 = (int)long0;
        System.out.println(long0);
    }

    public static void main(String[] array0) {
        test1(1112121L);
    }
}
