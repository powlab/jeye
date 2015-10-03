package org.powlab.jeye.tests.inner;


public class InnerClassTest0_3 {

    private final int x;

    public InnerClassTest0_3(int y) {
        this.x = y;
        new Inner1();
    }

    public class Inner1 {
        public Inner1() {
            System.out.println(x);
        }
    }
}
