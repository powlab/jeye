package org.powlab.jeye.tests.loop;


public class LoopTest51 {
    public static void dump(int length, long offset, int index) {
        for (int j= index; j < length; j+=16) {
            System.out.println(j);
        }
    }
}
