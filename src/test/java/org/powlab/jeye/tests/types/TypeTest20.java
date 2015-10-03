package org.powlab.jeye.tests.types;

import java.util.Map;

public interface TypeTest20<X, String> extends Map<X, String> {
    public static abstract class Impl2 implements TypeTest20<java.lang.String, Double> {
    }
    public static abstract class Impl1<String> implements TypeTest20<String, Double> {
    }
}
