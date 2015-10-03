package org.powlab.jeye.scenario.data;

public class Sample3 {

    public boolean synch1(Integer value, int k) {
        synchronized(value) {
            k++;
        }
        return false;
    }

    public boolean synch2(Integer value, int k) {
        synchronized(value) {
            k++;
            synchronized(this) {
                k--;
            }
        }
        return false;
    }

    public boolean synch3(Integer value, int k) {
        synchronized(value) {
            try {
                k++;
            } finally {
                k+=3;
            }
        }
        return false;
    }

    public void synch4(Object a, int b, int c, int d) {
        synchronized (a) {
            if (b > d) {
                return;
            }
            d = b / c;
            System.out.println(d);
        }
    }

    private int x;
    public int synch5() {
        synchronized (this) {
            try {
                return x;
            } catch (Throwable t) {
                throw t;
            }
        }
    }
}
