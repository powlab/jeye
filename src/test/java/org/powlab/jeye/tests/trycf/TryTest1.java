package org.powlab.jeye.tests.trycf;

public class TryTest1 {


    public void test1() {
        try {
            try {
                System.out.print(3);
                throw new NoSuchFieldException();
            } catch (NoSuchFieldException e) {
            }
            System.out.println("4");
        } finally {
            System.out.print("Finally!");
        }
        System.out.print(5);
    }
}
