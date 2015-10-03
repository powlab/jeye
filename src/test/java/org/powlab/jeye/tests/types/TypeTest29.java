package org.powlab.jeye.tests.types;

public class TypeTest29 {

    public void x(int[] i) {
    }

    public void x(short[] s) {
    }

    public void test() {
        short[] t = new short[] { 34 };
        x(t);
        int[] t2 = new int[] { 34 };
        x(t2);
    }

}
