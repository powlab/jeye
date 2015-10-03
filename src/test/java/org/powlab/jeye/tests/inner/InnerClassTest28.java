package org.powlab.jeye.tests.inner;


public class InnerClassTest28 {
    int y;

    public void foo(Integer i) {
        for (int x = 0;x<2;++x) {
            class Integer {
                private final java.lang.Integer inner;

                Integer(java.lang.Integer inner) {
                    this.inner = inner;
                }
            }

            Integer i1 = new Integer(i);
            Integer i2 = new Integer(2);
            java.lang.Integer i3 = i+1;
            System.out.println(i1);
            System.out.println(i2);
            System.out.println(i3);
        }

    }
}
