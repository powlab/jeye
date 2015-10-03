package org.powlab.jeye.tests.support;

public interface UnaryFunction<X,Y> {
    Y invoke(X arg);
}
