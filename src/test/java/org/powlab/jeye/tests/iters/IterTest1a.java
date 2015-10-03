package org.powlab.jeye.tests.iters;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class IterTest1a {

    public boolean test2(List<Object> list, Set<Object> set) {
        boolean result = false;
        Iterator<Object> e = list.iterator();
        while (e.hasNext()) {
            if (set.add(e.next())) {
                result = true;
            }
        }
        return result;
    }


}
