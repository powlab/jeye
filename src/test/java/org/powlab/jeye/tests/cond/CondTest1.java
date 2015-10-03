package org.powlab.jeye.tests.cond;

public class CondTest1 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println((b && a == (c = b) && b) || !c);
        return c;
    }

}
