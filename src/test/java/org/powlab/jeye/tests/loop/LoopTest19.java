package org.powlab.jeye.tests.loop;


public class LoopTest19 {

    private static int[] sizeTable;

    static int stringSize(final int i) {
        int n=0;
        for (;i > sizeTable[n];++n) {
        }
        return n + 1;
    }
}
