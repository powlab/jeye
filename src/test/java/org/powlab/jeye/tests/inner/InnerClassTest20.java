package org.powlab.jeye.tests.inner;


public class InnerClassTest20 {


    public void foo(Inner1.Inner2 y) {
        System.out.println(y.x);
    }

    public class Inner1 {
    public class Inner2 {
        private int x;

        public void foo() {
            System.out.println(x);
        }

    }
    }
}
