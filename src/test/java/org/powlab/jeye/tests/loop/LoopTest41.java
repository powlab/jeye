package org.powlab.jeye.tests.loop;

import java.util.Collection;


public class LoopTest41 {

    public <E> void test(Collection<E> coll ) {
        for (E c : coll) {
            System.out.println(c);
        }
        for (E d : coll) {
            System.out.println(d);
        }
    }
}
