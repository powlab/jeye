package org.powlab.jeye.tests.names;

import java.io.Serializable;

public class NameTest20 {

    public void foo(Object o, String s, Integer i, Number n, IllegalStateException e, int x) {
        Serializable n2 = null;
        if (x == 2) {
            n2 = e;
        }
        if (x == 3) {
            n2 = i;
        }
        System.out.println(n2);
    }

}
