package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally8 {

    void test1(String path) {
        try {
            int x = 3;
        } catch (NullPointerException t) {
            System.out.println("e1");
        } catch (UnsupportedOperationException t) {
            System.out.println("e2");
        } catch (IllegalStateException t) {
            System.out.println("e3");
        } finally {
            System.out.println("Fred");
        }
    }


}
