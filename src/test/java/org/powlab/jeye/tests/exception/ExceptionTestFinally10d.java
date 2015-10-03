package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally10d {

    int callWhichThrows() {
        throw new RuntimeException();
    }

    int test1(int x) {
        bob : {
            try {
                return callWhichThrows();
            } finally {
                break bob;
            }
        }
        System.out.println("TEST!");
        return 1;
    }


}
