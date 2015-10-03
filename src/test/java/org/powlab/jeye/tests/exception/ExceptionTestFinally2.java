package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally2 {

    void test1(String path) {
        try {
            int x = 3;
        } catch (NullPointerException t) {
            System.out.println("File Not found");
        } finally {
            System.out.println("Fred");
        }
    }


}
