package org.powlab.jeye.tests.inner;


public class InnerClassTest17c {

    private static final int x = 12;

    public InnerClassTest17c(int x) {
    }

    static int doX(int a, int b, int c) {
        return x+a+b+c;
    }

    public class Inner1<E> {

        public void getX(int y) {
            InnerClassTest17c.this.doX(y,1,y);
        }

    }
}
