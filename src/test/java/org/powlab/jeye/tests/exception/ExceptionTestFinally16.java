package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally16 {

    public void test1() {
        try {
            try {
                System.out.print(3);
                throw new NoSuchFieldException();
            } catch (NoSuchFieldException e) {
                System.out.println("Damn");
            }
        } finally {
            System.out.print("Finally!");
        }
        System.out.print(5);
    }
}
