package org.powlab.jeye.tests.iters;

import java.util.List;
import java.util.Set;


public class IterTest4 {


    public boolean test1(List<String> list, Set<Integer> set) {
        boolean result = false;
        for (String o : list) {
            result |= set.add(Integer.parseInt(o));
        }
        return result;
    }
}
