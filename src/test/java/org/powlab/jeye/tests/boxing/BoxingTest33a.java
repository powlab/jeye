package org.powlab.jeye.tests.boxing;


public class BoxingTest33a {

    public void test1(Horrid<Integer> reference0) {
        reference0.foo(null);
    }

    public void test(Horrid<Object> reference0) {
        reference0.foo(null);
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
