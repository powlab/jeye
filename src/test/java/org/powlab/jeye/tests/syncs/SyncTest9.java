package org.powlab.jeye.tests.syncs;

//TODO here: убрал 'final' boolean у аргументов a b c d
public class SyncTest9 {

    public int test(Object a, Object b, Object c, Object d) {
        synchronized (a) {
            return 1;
        }
    }
}
