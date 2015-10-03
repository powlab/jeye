package org.powlab.jeye.tests.syncs;

public class SyncTest4 {

    private int x;

    public void test1() {
        synchronized (this) {
            x = 3;
            synchronized (this) {
                x = 5;
            }
        }
        synchronized (this) {
            x = 3;
            synchronized (this) {
                x = 5;
            }
        }
    }
}
