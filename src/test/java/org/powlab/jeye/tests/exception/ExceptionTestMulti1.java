package org.powlab.jeye.tests.exception;


public class ExceptionTestMulti1 {

    public void foo() {
    }

    public void test1(int x) {
        try {
            foo();
        } catch (UnsupportedOperationException e) {
            System.out.println("JIM");
        } catch (NullPointerException | IllegalStateException e) {
            System.out.println("FRED");
        }
    }
}
