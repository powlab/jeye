package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally19d {

    public void test1(int x) {
        do {
            try {
                System.out.println("Oops");
                if (x == 12) break;
            } catch (RuntimeException t) {
                if (x == 3) break;
            }
        } while (true);
        System.out.print(5);
    }
}
