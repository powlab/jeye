package org.powlab.jeye.tests.varargs;

public class VarArgsTest6a {

    public static void x(int a, Object ... bs) {
    }

    public static void x(int a, Object bs) {
    }

    public void t() {
        Integer a = 2;
        Object b = new Object();
        Integer B = 5;
        x(3, (Object)new Object[]{a,b});
    }

}
