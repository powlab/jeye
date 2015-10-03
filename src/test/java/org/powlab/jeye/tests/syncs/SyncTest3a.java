package org.powlab.jeye.tests.syncs;

public class SyncTest3a {

    private int x;
    private Object y;

    public SyncTest3a() {
        x = 1;
    }

    public void test1() {
        synchronized (this) {
            try {
                if (x == 4) {
                    y = x;
                    return;
                }
                synchronized (y) {
                    x = 5;
                }
                x = 3;
                return;
            } catch (RuntimeException e) {
                x = 4;
            }
        }
    }
}
