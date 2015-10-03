package org.powlab.jeye.tests.loop;


public class LoopTest36 {

    public boolean test(boolean  a, boolean  b)
    {
        boolean res = true;
        boolean c = true;
        do {
            c = false;
            System.out.println(Boolean.valueOf(res));
        } while (res = (b && a == (c = b) && b && c));
        return c;
    }
}
