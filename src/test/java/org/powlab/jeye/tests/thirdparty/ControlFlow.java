package org.powlab.jeye.tests.thirdparty;

public class ControlFlow {
    public int foo(int i, int j) {
        while (true) {
            try {
                while (i < j)
                    i = j++ / i;
            } catch (RuntimeException re) {
                i = 10;
                continue;
            }
            break;
        }
        return j;
    }
}
