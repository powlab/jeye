package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally21a {

    public void fn() {
    }

    public void test1(int x) {
        do {
            try {
                try {
                    System.out.print(1);
                } catch (RuntimeException e) {
                }
            } finally {
                System.out.print(8);
            }
        } while (true);
    }
}
