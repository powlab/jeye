package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов a и b
public class ShortCircuitAssignTest3 {
    static boolean c;

    public boolean test1(boolean a, boolean b) {
        System.out.println((b && a == (c = b) && b) || !c);
        return c;
    }

    public boolean test2(boolean a, boolean b) {
        System.out.println((b && a == (c = b)) || !c);
        return c;
    }

    public boolean test3(boolean a, boolean b) {
        System.out.println((b && a) || ((c = b) || !c));
        return c;
    }

    public boolean test4(boolean a, boolean b) {
        System.out.println((b && (c = a)) || !c);
        return c;
    }

    public boolean test5(boolean a, boolean b) {
        System.out.println(b || (c = a) || !c);
        return c;
    }

    public boolean test6(boolean a, boolean b) {
        System.out.println(b && (c = a));
        return c;
    }

    public boolean test7(boolean a, boolean b) {
        System.out.println(b || (c = a));
        return c;
    }

    public boolean test8(boolean a, boolean b) {
        System.out.println(b && a == (c = b) && b && c);
        return c;
    }
}
