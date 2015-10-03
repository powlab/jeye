package org.powlab.jeye.tests.cast;



public class CastTest4 {

    public void t1() {
        long long1 = 2L;
        call(long1);
        call((int)long1);
        call((byte)long1);
        call((short)long1);
    }

    public void call(long long0) {
    }

    public void call(short short0) {
    }

}
