package org.powlab.jeye.tests.inner;


public class InnerClassTest22 {


    public void foo(Inner1.Inner2 y) {
        System.out.println(y.getX(1,2,3));
    }

    public class Inner1 {
    public class Inner2 {
        private int x;

        public void foo() {
            System.out.println(x);
        }

        private int getX(int a, int b, int c) {
            return a+b+c+x;
        }
    }
    }
}
