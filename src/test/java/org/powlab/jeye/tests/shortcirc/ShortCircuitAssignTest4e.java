package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов b
public class ShortCircuitAssignTest4e {
    public boolean test1(boolean a, boolean b, boolean[] c, boolean[] d) {
        System.out.println((b && (null != (c = a ? c : d)) &&
                a == ((c[(a && b || (d[0] = c[(a || c[2]) ? 0 : 1])) ? 1 : b ? 2 : 3] = b)) && b) ||
                !(a = c[0]));
        return a;
    }
}
