package org.powlab.jeye.tests.support;

import java.util.List;

public class Functional {
    public static <X> List<X> filter(List<X> in, Predicate<X> p) {
        return in; // hey, it doesn't have to work...
    }
}
