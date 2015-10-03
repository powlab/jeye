package org.powlab.jeye.tests.cond;

public class CondTest2 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println(a || (c ? a : b));
        return c;
    }

}
