package org.powlab.jeye.tests.loop;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class LoopTest11 {


    public int testNested(List<Object> list, Set<Object> set) {
        Iterator<Object> it1 = list.iterator();
        Iterator<Object> it2 = set.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            it1.next();
            it2.next();
        }

        return 4;
    }


}
