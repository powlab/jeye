package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally10e {

    int callWhichThrows() {
        throw new RuntimeException();
    }

    int test1(int x) {
        bob : {
            try {
                return callWhichThrows();
            } catch (Throwable t) {
            } finally {
                break bob;
            }
        }
        System.out.println("TEST!");
        return 1;
    }


}
