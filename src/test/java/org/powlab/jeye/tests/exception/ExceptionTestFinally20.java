package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally20 {

    public void test1(int x) {
        do {
            try {
                try {
//                    System.out.print(3);
                    throw new NoSuchFieldException();
                } catch (NoSuchFieldException e) {
                    System.out.println("Damn");
                }

                System.out.println("Oops");
            } finally {
                if (x == 3) break;
            }
        } while (true);
        System.out.print(5);
    }
}
