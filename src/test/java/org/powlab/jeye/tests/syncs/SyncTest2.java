package org.powlab.jeye.tests.syncs;

public class SyncTest2 {

    private int x;

    public void test1() {
        synchronized (this) {
            try {
                x = 3;
            } catch (RuntimeException e) {
                x = 4;
            } finally {
                x = 5;
            }
        }
    }
}
