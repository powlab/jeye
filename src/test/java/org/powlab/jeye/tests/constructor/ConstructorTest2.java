package org.powlab.jeye.tests.constructor;

public class ConstructorTest2 {

    public boolean test(final int x) {
        throw new UnsupportedOperationException("" + (x == 3 ? 3 : 1));
    }
}
