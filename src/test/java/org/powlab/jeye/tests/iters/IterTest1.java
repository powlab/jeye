package org.powlab.jeye.tests.iters;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class IterTest1 {

    public boolean test1(List<String> list, Set<Integer> set) {
        boolean result = false;
        for (String o : list) {
            result |= set.add(Integer.parseInt(o));
        }
        return result;
    }

    public boolean test1b(List list, Set set) {
        boolean result = false;
        for (Object o : list) {
            result |= set.add(o);
        }
        return result;
    }

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
