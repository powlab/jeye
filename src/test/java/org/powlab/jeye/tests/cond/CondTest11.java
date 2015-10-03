package org.powlab.jeye.tests.cond;

public class CondTest11 {

    public boolean test(int a, int b) {
        if (~a == b) {
            System.out.println("tesT");
            return false;
        }
        return true;
    }

    public boolean test(boolean a, boolean b) {
        if ((a ^ b) == b) {
            System.out.println("tesT");
            return false;
        }
        return true;
    }

    public boolean test2(int a, int b) {
        // Заменил: (b ^ -1) = ~b
        if (~a == ~b) {
            System.out.println("tesT");
            return false;
        }
        return true;
    }
}
