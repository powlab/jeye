package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов a и b и c
public class ShortCircuitAssignTest4 {
    public boolean test1(boolean a, boolean b, boolean[] c) {
        System.out.println((b && a == (c[0] = b) && b) || !c[0]);
        return c[0];
    }

    public boolean test2(boolean a, boolean b, boolean[] c) {
        System.out.println((b && a == (c[0] = b)) || !c[0]);
        return c[0];
    }

    public boolean test3(boolean a, boolean b, boolean[] c) {
        System.out.println((b && a) || ((c[0] = b) || !c[0]));
        return c[0];
    }

    public boolean test4(boolean a, boolean b, boolean[] c) {
        System.out.println((b && (c[0] = a)) || !c[0]);
        return c[0];
    }

    public boolean test5(boolean a, boolean b, boolean[] c) {
        System.out.println(b || (c[0] = a) || !c[0]);
        return c[0];
    }

    public boolean test6(boolean a, boolean b, boolean[] c) {
        System.out.println(b && (c[0] = a));
        return c[0];
    }

    public boolean test7(boolean a, boolean b, boolean[] c) {
        System.out.println(b || (c[0] = a));
        return c[0];
    }

    public boolean test8(boolean a, boolean b, boolean[] c) {
        System.out.println(b && a == (c[0] = b) && b && c[0]);
        return c[0];
    }
}
