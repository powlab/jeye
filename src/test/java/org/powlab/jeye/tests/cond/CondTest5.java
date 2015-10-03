package org.powlab.jeye.tests.cond;

public class CondTest5 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println((b ? c : a) || (c ? a : b));
        return c;
    }

}
