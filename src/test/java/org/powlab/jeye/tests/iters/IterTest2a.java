package org.powlab.jeye.tests.iters;

import java.util.Iterator;
import java.util.Map;


public class IterTest2a {


    public boolean test1(Map<String, Integer[]> map) {
        boolean result = false;
        Iterator<Map.Entry<String, Integer[]>> iterator = map.entrySet().iterator();
        return true;
    }


}
