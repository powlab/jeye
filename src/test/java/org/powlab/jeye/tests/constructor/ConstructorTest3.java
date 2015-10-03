package org.powlab.jeye.tests.constructor;

public class ConstructorTest3 {

    public boolean test(final int x) {
        throw new UnsupportedOperationException(x == 3 ? "Three" : "1");
    }
}
