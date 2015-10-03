package org.powlab.jeye.tests.cond;

public class CondTest3 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println(a || (c = b));
        return c;
    }

}
