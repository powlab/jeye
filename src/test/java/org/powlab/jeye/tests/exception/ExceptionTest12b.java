package org.powlab.jeye.tests.exception;


public class ExceptionTest12b {


    public static void foo() {

        try {

            throw new Exception();

        } catch (Exception e) {

            String s = e.getMessage();
            String s2 = "fred" + s;
            System.out.println(s2);
        }

    }
}
