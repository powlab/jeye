package org.powlab.jeye.tests.loop;


public class LoopTest52 {
    public static void dump(int length, long offset, int index) {
        if (offset == 1) {
            while (index < length) {
                System.out.println(index++);
                offset++;
            }
        }
        System.out.println("Done");
    }
}
