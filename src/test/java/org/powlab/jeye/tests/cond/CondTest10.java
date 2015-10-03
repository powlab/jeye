package org.powlab.jeye.tests.cond;

public class CondTest10 {

    public boolean test(boolean a, boolean b, boolean c) {
        if (!(a ^ b)) {
            System.out.println("tesT");
            return false;
        }
        return true;
    }

}
