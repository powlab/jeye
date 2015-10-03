package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally9 {

    void test1(String path) {
        try {
            int x = 3;
        } finally {
            System.out.println("Fred");
        }
    }


}
