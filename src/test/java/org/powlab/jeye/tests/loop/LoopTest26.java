package org.powlab.jeye.tests.loop;

import java.util.Iterator;
import java.util.List;

public class LoopTest26 {

    public boolean cannotMoveIteratorVariableDeclaration(List<Integer> l2) {
        Iterator<Integer> i$ = l2.iterator();
        while (i$.hasNext()) {
            final Integer i2 = i$.next();
            System.out.println(i2);
            System.out.println(i2 == 4 ? i$ : "b");
        }
        /*
         * blah
         *
         */
        return true;
    }
}
