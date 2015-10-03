package org.powlab.jeye.tests.exception;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public abstract class ExceptionTestFinally19c {

    public void callWhichMightThrow() { throw new IllegalStateException(); }

    public void test1() {

        for (int y=0;y<10;++y) {
            do {
                try {
                    callWhichMightThrow();
                } catch (RuntimeException t) {
                    if (y < 100) break;
                }
            } while (true);
            System.out.print(y);
        }
        System.out.println("FIN");
    }
}
