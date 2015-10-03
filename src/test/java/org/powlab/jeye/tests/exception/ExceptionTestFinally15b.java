package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally15b {

    void test1(String path) {
        try {
            if (path == null) return;
            int x = 4;
        } catch (NullPointerException t) {
            System.out.println("File Not found");
            if (path == null) return;
            throw t;
        } finally {
            System.out.println("Fred");
            if (path == null) throw new IllegalStateException();
        }
    }


}
