package org.powlab.jeye.tests.varargs;

public class VarArgsTest1c {

    public void x(int a, Integer ... bs) {
    }

    public void x(int a, int b1, int b2, int b3) {
    }

    public void t() {
        Integer a = 2;
        int b = 5;
        x(3, a, a, a);
        x(3, a, null, a);
        x(3, new Integer[]{a,a,a});
    }

}
