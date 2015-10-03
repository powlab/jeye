package org.powlab.jeye.tests.precedence;

public class PrecedenceTest3 {

    public boolean test1(boolean a, boolean b, boolean c, boolean d) {
        return a ? b : b ? c : d;
    }

    public boolean test2(boolean a, boolean b, boolean c, boolean d) {
        return (a ? b : b) ? c : d;
    }

    public boolean test3(boolean a, boolean b, boolean c, boolean d) {
        return a ? b : (b ? c : d);
    }
}
