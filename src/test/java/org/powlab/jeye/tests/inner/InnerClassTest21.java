package org.powlab.jeye.tests.inner;


public class InnerClassTest21 {


    public void foo(Inner2 y) {
        System.out.println(y.x);
    }

    public class Inner2 {
        private int x;

        public void foo() {
            System.out.println(x);
        }

    }
}
