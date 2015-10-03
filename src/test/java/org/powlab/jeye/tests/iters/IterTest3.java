package org.powlab.jeye.tests.iters;

import java.util.Map;


public class IterTest3<T> {


    public boolean test1(Map<T, Integer[]> map) {
        boolean result = false;
        for (Map.Entry<T, Integer[]> o : map.entrySet()) {
            result |= o.getKey().equals(o.getValue());
        }
        return result;
    }


}
