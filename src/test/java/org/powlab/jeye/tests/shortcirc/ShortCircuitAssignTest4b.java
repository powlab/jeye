package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов a и b
public class ShortCircuitAssignTest4b {
    public boolean test1(boolean a, boolean b, boolean[] c, boolean [] d) {
        System.out.println((b && (null != (c = a ? c : d)) && a == (c[0] = b) && b) || !c[0]);
        return c[0];
    }
}
