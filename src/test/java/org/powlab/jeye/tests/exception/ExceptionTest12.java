package org.powlab.jeye.tests.exception;


public class ExceptionTest12 {


    public static void foo() {

        try {

            throw new Exception();

        } catch (Exception e) {

            String s = e.getMessage();

        }

    }
}
