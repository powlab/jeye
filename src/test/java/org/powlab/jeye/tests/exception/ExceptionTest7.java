package org.powlab.jeye.tests.exception;


public class ExceptionTest7 {

    void test1(String path) {
        try {
            try {
                int x = 1;
            } catch (Throwable t) {
                throw t;
            }
            System.out.println("Fred");
        } catch (NullPointerException t) {
            System.out.println("Fred");
        }
    }


}
