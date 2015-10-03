package org.powlab.jeye.tests.usages;

public class UsageTest3 {

    public void test1(boolean a) {
        char b;
        if (a) {
            System.out.println("A!");
            b = 'a';
        } else {
            b = 'b';
        }
        if (a ||!a) { // woo but the tautology still gets compiled in.
            System.out.println("B is " + b);
        }
    }

}
