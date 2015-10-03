package org.powlab.jeye.tests.exception;

public class ExceptionTestFinally26e {

    public void fn() { }

    public boolean test(final int x) {
        try {
            if (x==0) return true;
            System.out.println("Not here");
        } finally {
            throw new UnsupportedOperationException();
        }
    }
}
