package org.powlab.jeye.tests.syncs;

//TODO here: убрал final у a аргумента
public class SyncTest11 {

    public void test(Object a, int b, int c, int d) {
        synchronized (a) {
            if (b > d) {
                return;
            }
            d = b / c;
            System.out.println(d);
        }
    }
}
