package org.powlab.jeye.tests.loop;

import java.util.Collection;


public class LoopTest43 {

    public <E> E test(Collection<E> coll ) {
        for (E c : coll) {
            System.out.println(c);
        }
        for (E c : coll) {
            System.out.println(c);
        }
        return null;
    }
}
