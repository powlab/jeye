package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally10f {

    void callWhichMightThrow() {
    }

    int test1(boolean a) {
        bob : {
            try {
                callWhichMightThrow();
            } catch (Exception e){
                if (a) break bob;
                System.out.println("Foo");
            }
            System.out.println("ooooh");
        }
        System.out.println("BAR");
        return 1;
    }


}
