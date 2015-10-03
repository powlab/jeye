package org.powlab.jeye.tests.cast;



public class CastTest6 {

    public void t1() {
        int int1 = 2;
        call(int1);
        call(int1);
        call((long)int1);
    }

    public void call(long long0) {
    }

    public void call(int int0) {
    }

}
