package org.powlab.jeye.tests.exception;

import java.io.IOException;
import java.util.List;


public class ExceptionSpecTest2 {

    public <X> List<X> func(X x) throws IllegalStateException {
        throw new IllegalStateException();
    }

    public <X> List<X> func2(X x) throws InnerException {
        throw new InnerException();
    }

    public static class InnerException extends RuntimeException {
    }
}
