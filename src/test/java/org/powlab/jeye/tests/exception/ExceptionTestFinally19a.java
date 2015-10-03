package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally19a {

    public void test1(int x) {
        do {
            try {
                System.out.println("Oops");
            } catch (RuntimeException t) {
                if (x == 3) break;
            }
        } while (true);
        System.out.print(5);
    }
}
