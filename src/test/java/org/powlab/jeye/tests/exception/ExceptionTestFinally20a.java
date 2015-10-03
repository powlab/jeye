package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally20a {

    public void callWhichMightThrow() throws NoSuchFieldException {
    }

    public void test1(int x) {
        do {
            try {
                try {
                    System.out.print(3);
                    callWhichMightThrow();
                } catch (NoSuchFieldException e) {
                    System.out.println("Damn");
                }
                System.out.println("Oops");
            } finally {
                System.out.println("IN FINALLY");
                if (x == 3) break;
            }
        } while (true);
        System.out.print(5);
    }
}
