package org.powlab.jeye.tests.syncs;

public class SyncTest1a {

    private int x;

    public int test1() {
        synchronized (this) {
            try {
                return x;
            } catch (Throwable t) {
                throw t;
            }
        }
    }
}
