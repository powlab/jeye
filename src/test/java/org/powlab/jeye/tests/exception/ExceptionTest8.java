package org.powlab.jeye.tests.exception;


public class ExceptionTest8 {

    void test1(String path) {
        try {
            System.out.println(path);
        } catch (NullPointerException t) {
            System.out.println(t);
        } catch (ArithmeticException t) {
            System.out.println(t);
        }
    }


}
