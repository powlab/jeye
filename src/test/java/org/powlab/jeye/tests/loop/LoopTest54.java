package org.powlab.jeye.tests.loop;


public class LoopTest54 {
    public static void dump(boolean b , int i1 , int i2) {
        for (int i = 0,j=0; i < 10; j+=i, i++) {
            System.out.println("Done" + i + j);
        }
    }

    public static void dump2(boolean b , int i1 , int i2) {
        for (int i = 0,j=0; i < 10; i++, j+=i) {
            System.out.println("Done" + i + j);
        }
    }
}
