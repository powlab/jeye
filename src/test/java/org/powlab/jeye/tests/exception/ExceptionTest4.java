package org.powlab.jeye.tests.exception;


public class ExceptionTest4 {

    String x() {
        return null;
    }

    void test1(String[] path) {
        String s = x();
        try {
            String b = x();
            try {
                System.out.println("S1" + s.length());

            } catch (NullPointerException e) {
                System.out.println("NPE1 " + b.length());
                throw e;
            }
        } catch (NullPointerException e) {
            System.out.println("NPE2");
        } catch (IllegalArgumentException e) {
            System.out.println("NPE2");
        }
    }


}
