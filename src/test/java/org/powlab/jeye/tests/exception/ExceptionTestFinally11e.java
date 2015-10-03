package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally11e {

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
        for (int x= 0;x<3;++x) {
            System.out.println("FOOO");
        }
    }



}
