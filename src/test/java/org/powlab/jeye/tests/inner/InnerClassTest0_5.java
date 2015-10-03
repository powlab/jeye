package org.powlab.jeye.tests.inner;


public class InnerClassTest0_5 {

    private final Integer x = 3;
    private int y;

    public void foo() {
        new Inner1();
    }

    public class Inner1 {
        public Inner1() {
            System.out.println(x);
            System.out.println(y);
        }

        public void foo() {
            System.out.println(x);
            System.out.println(y);
            System.out.println(Math.max(x, y));
        }
    }
}
