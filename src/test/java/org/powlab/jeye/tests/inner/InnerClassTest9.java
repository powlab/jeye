package org.powlab.jeye.tests.inner;


public class InnerClassTest9 {

    public int x = 3;

    public class Inner1 {

        public int tweakX(int y) {
            x += y;
            return x;
        }

    }
}
