package org.powlab.jeye.tests.syncs;

//TODO here: убрал final у a аргумента
public class SyncTest12 {

    public void test (Object a,int b, int c, int d) {
        synchronized (a) {
            d = b / c;
            System.out.println(d);
        }
        synchronized (a) {
            d = b * c;
            System.out.println(d);
        }
    }
}
