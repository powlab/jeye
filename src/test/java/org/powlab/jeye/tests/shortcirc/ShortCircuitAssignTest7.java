package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов a, b, c
public class ShortCircuitAssignTest7 {
    static int i;
    int j;

    private static int index() {
        return 0;
    }

    public boolean test1(boolean a, boolean b, boolean[] c) {
        System.out.println((a && c[0] == (c[0] = b) && b) || !c[0]);
        return c[0];
    }

    public boolean test2(boolean a, boolean b, boolean[] c) {
        System.out.println((b && a == (c[0] = !c[0])) || !c[0]);
        return c[0];
    }

    public boolean test3(boolean a, boolean b, boolean[] c) {
        System.out.println((a && c[index()] == (c[index()] = b) && b) || !c[index()]);
        return c[index()];
    }

    public boolean test4(boolean a, boolean b, boolean[] c) {
        System.out.println((b && a == (c[index()] = !c[index()])) || !c[index()]);
        return c[index()];
    }

    public boolean test5(boolean a, boolean b, boolean[] c) {
        System.out.println((a && c[index() + i] == (c[index() + j] = b) && b) || !c[index() + j]);
        return c[index() + i];
    }

    public boolean test6(boolean a, boolean b, boolean[] c) {
        System.out.println((b && a == (c[index() + i] = !c[index() + j])) || !c[index() + j]);
        return c[index() + i];
    }
}
