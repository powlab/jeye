package org.powlab.jeye.tests.loop;


public class LoopTest39 {

    public void test(int [] xs)
    {
        for (int x : xs) {
            System.out.println(x);
            for (int y : xs) {
                System.out.println(y);
            }
        }
        for (int x : xs) {
            System.out.println(x);
        }
    }
}
