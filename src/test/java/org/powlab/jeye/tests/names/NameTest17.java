package org.powlab.jeye.tests.names;

public class NameTest17 {

    public void foo(Object o, String s, Integer i, Number n, Double d, int x) {
        Number n2 = null;
        if (x == 1) {
            n2 = n;
        }
        if (x == 3) {
            n2 = i;
        }
        System.out.println(n2);
    }

}
