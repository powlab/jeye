package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally20c {

    public int test1(int x) {
        do {
            try {
                System.out.println("Oops");
            } finally {
                if (x == 3) break;
            }
        } while (x < 45);
        System.out.print(5);
        return 1;
    }
}
