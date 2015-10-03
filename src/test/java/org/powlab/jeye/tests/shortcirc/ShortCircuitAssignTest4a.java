package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов a и b и c
public class ShortCircuitAssignTest4a {
    public boolean test1(boolean a, boolean b, boolean[] c) {
        System.out.println(b && a == (c[0] = b) && b);
        return c[0];
    }
}
