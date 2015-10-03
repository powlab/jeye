package org.powlab.jeye.tests.syncs;

public class SyncTest3b {

    private int x;
    private Object y;
    public SyncTest3b() {
        x = 1;
    }

    public void test1() {
        if (true) {
            synchronized (this) {
                if (!false) {
                    try {
                        synchronized (y) {
                            int x = 5;
                            y = x;
                        }
                    } catch (Throwable e) {
                        throw e;
                    }
                }
            }
        }
    }
}
