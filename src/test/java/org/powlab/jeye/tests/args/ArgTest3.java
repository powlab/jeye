package org.powlab.jeye.tests.args;

public class ArgTest3 {

    public void test2(int a, int b, double c, int d) {
        c++;
        if (++c > b) {
            System.out.println("A" + a + b +c + d);
        }
        if (c++ < a) {
            System.out.println("A" + a + b +c + d);
        }
        if (c < a) {
            System.out.println("A" + a + b +c + d);
        }
    }

}
