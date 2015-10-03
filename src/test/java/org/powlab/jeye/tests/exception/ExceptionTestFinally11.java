package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally11 {

    void test1(String path) {
        bob : {
            try {
                int x = 3;
                if (path == null) break bob;
            } finally {
                System.out.println("Fred");
            }
        }
    }


}
