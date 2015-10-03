package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally11c {

    void test1(String path) {
        bob : {
            try {
                int x = 3;
                if (path == null) {
                    System.out.println("a");
                    break bob;
                }
            } finally {
                System.out.println("Fred");
            }
        }
    }


}
