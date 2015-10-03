package org.powlab.jeye.tests.cond;

public class CondTest7 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println(a && (c ? (b ? a : c) : (b ? c : a)));
        return c;
    }

}
