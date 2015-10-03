package org.powlab.jeye.tests.usages;

public class UsageTest2 {

    public void test1(boolean a) {
        char b;
        if (a) {
            System.out.println("A!");
            b = 'a';
        } else {
            b = 'b';
        }
        System.out.println("B is " + b);
    }

}
