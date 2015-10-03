package org.powlab.jeye.tests.exception;

public class ExceptionTestFinally28 {

    boolean a;

    public Object test(final int x) {
        try {
            return null;
        }
        finally {
            this.a = true;
        }
    }
}
