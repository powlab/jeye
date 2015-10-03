package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally12b {

    void test1(String path) {
       try {
           int x = 3;
       } catch (RuntimeException t) {
           System.out.println("Fred");
           throw t;
       }
       System.out.println("Fred");
    }


}
