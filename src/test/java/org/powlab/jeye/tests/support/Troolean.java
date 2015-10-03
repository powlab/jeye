package org.powlab.jeye.tests.support;

public enum Troolean {
    NEITHER,
    FIRST,
    SECOND,
    BOTH;

    public static Troolean get(boolean a, boolean b) {
        if (a) {
            if (b) return BOTH;
            return FIRST;
        }
        if (b) return SECOND;
        return NEITHER;
    }
}
