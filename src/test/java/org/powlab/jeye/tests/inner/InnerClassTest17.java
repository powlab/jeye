package org.powlab.jeye.tests.inner;


public class InnerClassTest17 {

    private final int x;

    public InnerClassTest17(int x) {
        this.x = x;
    }

    private int getX(int a, int b, int c) {
        return x+a+b+c;
    }

    public class Inner1<E> {

        public int getX(int y) {
            return 2 + y + InnerClassTest17.this.getX(y,1,y);
        }

    }
}
