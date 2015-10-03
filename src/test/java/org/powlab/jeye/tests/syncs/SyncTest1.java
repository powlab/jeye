package org.powlab.jeye.tests.syncs;

public class SyncTest1 {

    private int x;

    public void test1() {
        synchronized (this) {
            x = 3;
        }
    }
}
