package org.powlab.jeye.tests.inner;


public class InnerClassTest17e {

    private static final int x = 12;

    public InnerClassTest17e(int x) {
    }

    static int getX(int a, int b, int c) {
        return x+a+b+c;
    }

    public class Inner2 {
    public class Inner1<E> {

        public int getX(int y) {
            return 2 + y + InnerClassTest17e.this.getX(y,1,y);
        }

    }
    }
}
