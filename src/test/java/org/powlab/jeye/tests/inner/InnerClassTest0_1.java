package org.powlab.jeye.tests.inner;


public class InnerClassTest0_1 {

    int x = 3;

    public void foo() {
        new Inner1();
    }

    public class Inner1 {
        public Inner1() {
            System.out.println(x);
        }
    }
}
