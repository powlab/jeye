package org.powlab.jeye.tests.cast;



public class CastTest9 {

    public void t1() {
        Integer reference1 = 2;
        call(reference1);
        call((long)reference1);  // same byte code as call(reference1)
        call2(reference1);
        call2((long)reference1); // same byte code as call2(reference1)
        call2(reference1);
    }

    public void call(long long0) {
    }

    public void call2(double double0) {
    }

}
