package org.powlab.jeye.tests.syncs;

public class SyncTest3c {

    private int x;
    private Object y;

    public SyncTest3c() {
        x = 1;
    }

    public void test1() {
        if (true) {
            synchronized (this) {
                try {
                    synchronized (y) {
                        int x = 5;
                    }
                } catch (Throwable e) {
                    throw e;
                }
            }

        }
    }
}
