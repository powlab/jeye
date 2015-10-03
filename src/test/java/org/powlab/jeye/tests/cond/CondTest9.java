package org.powlab.jeye.tests.cond;

public class CondTest9 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println(b || (c = a));
        return c;
    }

}
