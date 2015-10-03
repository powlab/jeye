package org.powlab.jeye.tests.cond;

public class CondJumpTest2 {

    // добавил println, так как срабатывает оптимизатор (в лучшую сторону)
    public boolean test(boolean a, boolean b) {
        boolean c;
        if (b && a == (c = b) && b && c) {
            System.out.println("keep if");
            return true;
        }
        return false;
    }
}
