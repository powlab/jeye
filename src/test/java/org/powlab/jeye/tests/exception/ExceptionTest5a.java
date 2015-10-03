package org.powlab.jeye.tests.exception;


public class ExceptionTest5a {

    public int test (final Object a, final Object b, final Object c, final Object d) {
        try {
            try {
                return 1;
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
