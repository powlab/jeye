package org.powlab.jeye.tests.cond;

public class CondTest8 {

    public boolean test(boolean a, boolean b, boolean c) {
        System.out.println(c ? b ? a : c : b);
        return c;
    }

}
