package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally10b {

    int test1(int x) {
        try {
            return x;
        } finally {
            return x+1;
        }
    }


}
