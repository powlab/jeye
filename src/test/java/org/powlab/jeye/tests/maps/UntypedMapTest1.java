package org.powlab.jeye.tests.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UntypedMapTest1 {
    private Map memberMap = new TreeMap();

    public void test() {
        Map m = new HashMap();

        m.put("fred", 1);

        memberMap.putAll(m);
    }
}
