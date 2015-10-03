package org.powlab.jeye.tests.syncs;

public class SyncTest2a {

    private int x;

    private boolean called;

    private int OnlyCallOnce() {
        if (!called) {
            called = true;
            return 10;
        }
        throw new IllegalStateException();
    }

    public void test1() {
        synchronized (this) {
            try {
                x /= 0;
            } catch (RuntimeException e) {
                x = 4;
            } finally {
                x = OnlyCallOnce();
            }
        }
    }
}
