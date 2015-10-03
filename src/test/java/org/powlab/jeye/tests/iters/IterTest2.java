package org.powlab.jeye.tests.iters;

import java.util.Map;


public class IterTest2 {


    public boolean test1(Map<String, Integer[]> map) {
        boolean result = false;
        for (Map.Entry<String, Integer[]> o : map.entrySet()) {
            result |= o.getKey().equals(o.getValue());
        }
        return result;
    }


}
