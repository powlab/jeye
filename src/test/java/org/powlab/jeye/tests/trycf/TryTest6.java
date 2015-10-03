package org.powlab.jeye.tests.trycf;

public class TryTest6 {

    public void functionWhichMightThrow() {
    }

    public void foo() {
        try {
            functionWhichMightThrow();
            System.out.println("Fred");
        } catch (Exception e) {
        }
        System.out.println("Jim");
    }
}
