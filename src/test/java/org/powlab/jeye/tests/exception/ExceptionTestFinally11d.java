package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally11d {

    void test1(String path) {
        bob : {
            try {
                int x = 3;
                if (path == null) break bob;
                System.out.println(path);
            } finally {
                System.out.println("Fred");
            }
        }
    }


}
