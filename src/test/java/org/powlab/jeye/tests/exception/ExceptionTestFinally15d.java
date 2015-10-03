package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally15d {

    void test1(String path) {
        try {
            if (path == null) return;
        } finally {
            System.out.println("Fred");
            if (path == null) throw new IllegalStateException();
        }
        System.out.println("Bob");
    }


}
