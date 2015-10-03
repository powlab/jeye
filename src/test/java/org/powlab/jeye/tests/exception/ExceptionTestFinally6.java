package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally6 {

    void test1(String path) {
        try {
            int x = 3;
        } catch (NullPointerException t) {
            System.out.println("File Not found");
            if (path == null) return;
            if (path.equals("")) throw new IllegalStateException();
            throw t;
        } finally {
            System.out.println("Fred");
        }
    }


}
