package org.powlab.jeye.tests.shortcirc;

//TODO here: убрал 'final' boolean у аргументов a и b
public class ShortCircuitAssignTest1a {
    public boolean test1(boolean a, boolean b, boolean c) {
        System.out.println(b && a == (c = b) && b);
        return c;
    }
}
