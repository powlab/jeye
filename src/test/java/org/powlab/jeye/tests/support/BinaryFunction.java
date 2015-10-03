package org.powlab.jeye.tests.support;

public interface BinaryFunction<X, Y, R> {
    R invoke(X arg1, Y arg2);
}
