package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally5 {

    void test1(String path) {
        try {
            int x = 3;
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
