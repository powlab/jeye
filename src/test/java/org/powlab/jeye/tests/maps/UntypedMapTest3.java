package org.powlab.jeye.tests.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UntypedMapTest3<T> {
    private Map<T,?> memberMap = new TreeMap();

    public void test() {
        Map m = new HashMap();

        m.put("fred", 1);
        m.put(2, "fred");

        memberMap.putAll(m);
    }
}
