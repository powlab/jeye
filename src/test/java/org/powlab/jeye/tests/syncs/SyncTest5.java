package org.powlab.jeye.tests.syncs;

public class SyncTest5 {

    private int x;

    public void test1() {
        if (x == 4) {
            synchronized (this) {
                x = 3;
            }
        }
    }
}
