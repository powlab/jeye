package org.powlab.jeye.tests.trycf;

public class TryTest2 {


    public void test1(int x) {
        try {
            if (x < 3) {
                System.out.println("a");
            } else {
                System.out.println("b");
            }
        } catch (RuntimeException e) {
            System.out.println("e");
        }
        System.out.print(5);
    }
}
