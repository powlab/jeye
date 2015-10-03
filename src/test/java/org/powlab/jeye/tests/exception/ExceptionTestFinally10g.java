package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally10g {

    void callWhichMightThrow() {
    }

    int test1(int x) {
        bob : {
            if (x % 3 == 0) {
                System.out.println("Foo");
                break bob;
            }
            System.out.println("ooooh");
        }
        System.out.println("TEST!");
        return 1;
    }


}
