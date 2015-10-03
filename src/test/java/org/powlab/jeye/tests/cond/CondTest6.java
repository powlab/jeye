package org.powlab.jeye.tests.cond;

public class CondTest6 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println(a && (c ? a : b));
        return c;
    }

}
