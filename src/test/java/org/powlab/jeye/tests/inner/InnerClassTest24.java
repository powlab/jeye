package org.powlab.jeye.tests.inner;

import org.powlab.jeye.tests.support.Predicate;


public class InnerClassTest24 {
    int y;

    public Predicate<Double> foo(int i) {
        class Bob implements Predicate<Double> {
            @Override
            public boolean test(Double in) {
                return in < y;
            }
        }
        int x = 1;
        if (x < 3) {
            return new Bob();
        } else {
            if (x > 5) {
                return null;
            } else {
                return new Bob();
            }
        }
    }
}
