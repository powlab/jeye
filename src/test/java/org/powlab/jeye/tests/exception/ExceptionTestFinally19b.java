package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally19b {

    public void test1(int x) {
        do {
            try {
                System.out.println("Oops");
            } catch (Throwable t) {
                if (x == 3) break;
            }
        } while (true);
        System.out.print(5);
    }
}
