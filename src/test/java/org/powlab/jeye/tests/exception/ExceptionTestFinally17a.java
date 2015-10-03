package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally17a {

    public void test1(int x) {
        do {
            try {
                try {
                    System.out.print(3);
                    throw new IllegalStateException();
                } catch (IllegalStateException e) {
                    System.out.println("Damn");
                }
                System.out.println("Oops");
            } finally {
                break;
            }
        } while (true);
        System.out.print(5);
    }
}
