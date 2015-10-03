package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally21 {

    public void fn() {
    }

    public void test1(int x) {
        do {
            try {
                try {
                    System.out.print(1);
                    try {
                        System.out.print(2);
                        fn();
                        System.out.print(3);
                    } catch (IllegalStateException e) {
                        System.out.print(4);
                    }
                    System.out.print(5);
                } catch (RuntimeException e) {
                    System.out.print(6);
                }
                System.out.print(7);
            } finally {
                System.out.print(8);
            }
        } while (true);
    }
}
