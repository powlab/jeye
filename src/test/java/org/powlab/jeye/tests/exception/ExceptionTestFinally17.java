package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally17 {

    public void test1(int x) {
        do {
            try {
                try {
                    System.out.print(3);
                    if (x==1) throw new NoSuchFieldException();
                } catch (NoSuchFieldException e) {
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
