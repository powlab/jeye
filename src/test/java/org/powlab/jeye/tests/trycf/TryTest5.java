package org.powlab.jeye.tests.trycf;

public class TryTest5 {

    public void functionWhichMightThrow() {
    }

    public boolean test() {
        return false;
    }

    public void foo()  {
        while(true) {
            do {
                try {
                    functionWhichMightThrow();
                } catch (Exception e) {
                    break;
                }
            } while (test());
            System.out.println("A");
        }
    }
}
