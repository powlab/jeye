package org.powlab.jeye.tests.cast;



public class CastTest8 {

    public void t1() {
        int s = 2;
        call(s);
        call((long)s);
    }

    public void call(long l) {
    }

}
