package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally20d {

    public int test1(int x) {
        do {
            try {
                if (x==1) return 1;
                System.out.println("Oops");
                if (x==23) return 1;
                System.out.println("Oops");
                if (x==25) return 1;
            } finally {
                if (x == 3) break;
            }
        } while (x < 45);
        System.out.print(5);
        return 1;
    }
}
