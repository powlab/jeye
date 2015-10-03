package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов b
public class ShortCircuitAssignTest4f {
    public boolean test1(boolean a, final boolean b, boolean c, boolean  d) {
        System.out.println((c = a ? c : d) && ((c = a) ? c : d) );
        return a;
    }
}
