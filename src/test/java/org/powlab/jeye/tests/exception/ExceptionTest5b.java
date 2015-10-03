package org.powlab.jeye.tests.exception;


public class ExceptionTest5b {

    public int test (final Object a, final Object b, final Object c, final Object d) {
            try {
                return 1;
            } catch (Exception e) {
                try {
                    throw e;
                } catch (Exception e2) {
                    throw e2;
                }
            }
    }
}
