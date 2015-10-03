package org.powlab.jeye.tests.inner;

import org.powlab.jeye.tests.support.Predicate;


public class InnerClassTest29 {
    int y;

    public <T> Predicate<T> foo(int i) {
        int x = 1;
        if (x < 3) {
            class Bob<T> implements Predicate<T> {
                @Override
                public boolean test(T in) {
                    return in.hashCode() < 100;
                }
            }
            return new Bob<T>();
        } else {
            if (x > 5) {
                return null;
            } else {
                class Bob<T> implements Predicate<T> {
                    @Override
                    public boolean test(T in) {
                        return in != null;
                    }
                }
                return new Bob<T>();
            }
        }
    }
}
