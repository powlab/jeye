package org.powlab.jeye.tests.inner;


public class InnerClassTest17a {

    private final int x;

    public InnerClassTest17a(int x) {
        this.x = x;
    }

    private void doX(int a, int b, int c) {
        System.out.println( x+a+b+c);
    }

    public class Inner1<E> {

        public void getX(int y) {
            InnerClassTest17a.this.doX(y,1,y);
        }

    }
}
