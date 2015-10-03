package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally10a {

    void test1(String path) {
        try {
            int x = 3;
        } finally {
            throw new IllegalStateException();
        }
    }


}
