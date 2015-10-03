package org.powlab.jeye.tests.boxing;


public class BoxingTest33 {

    public void test1(Horrid<Integer> reference0) {
        reference0.foo((Number)1);
        reference0.foo((Integer)1);
    }

    public void test(Horrid<String> reference0) {
        reference0.foo((Number)1);
        reference0.foo((Integer)1);
    }

    public static class Horrid<T> {

        boolean foo(int int0) {
            return false;
        }

        boolean foo(T reference0) {
            return false;
        }

        boolean foo(Number reference0) {
            return true;
        }
    }
}
