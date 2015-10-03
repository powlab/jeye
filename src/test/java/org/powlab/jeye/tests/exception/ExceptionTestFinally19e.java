package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally19e {

    public void test1(int x) {
        do {
            try {
                System.out.println("Oops");
            } finally {
                if (x == 3) continue;
            }
            break;
        } while (true);
        System.out.print(5);
    }
}
