package org.powlab.jeye.tests.inner;

import org.powlab.jeye.tests.support.Predicate;


public class InnerClassTest25 {
    int y;

    public Predicate<Double> foo(int i) {
        int x = 1;
        if (x < 3) {
            class Bob implements Predicate<Double> {
                @Override
                public boolean test(Double in) {
                    return in < y;
                }
            }
            return new Bob();
        } else {
            if (x > 5) {
                return null;
            } else {
                class Bob implements Predicate<Double> {
                    @Override
                    public boolean test(Double in) {
                        return in > y;
                    }
                }
                return new Bob();
            }
        }
    }
}
