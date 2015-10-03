package org.powlab.jeye.tests.loop;


public class LoopTest53 {
    public static void dump(boolean b , int i1 , int i2) {
        for (int i = 0, j =  b ? i1 : i2; i < i1; i++, j++) {
            System.out.println("Done" + i + j);
        }
    }
}
