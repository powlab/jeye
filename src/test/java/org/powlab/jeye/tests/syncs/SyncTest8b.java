package org.powlab.jeye.tests.syncs;

// TODO here: убрал 'final' boolean у аргументов a b c d
public class SyncTest8b {

    public int test(Object a, Object b, Object c, Object d) {
        synchronized (a) {
            try {
                return 1;
            } catch (Exception e) {
                throw e;
            }
        }
    }
}