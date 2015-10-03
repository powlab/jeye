package org.powlab.jeye.tests.varargs;

public class VarArgsTest1b {

    public void x(int a, Object ... bs) {
    }

    public void x(int a, String b1, String b2) {
    }

    public void t() {
        x(3,"a",3);
    }

}
