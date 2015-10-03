package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally10c {

    int test1(int x) {
        bob : {
            try {
                return 2;
            } finally {
                break bob;
            }
        }
        System.out.println("TEST!");
        return 1;
    }


}
