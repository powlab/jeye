package org.powlab.jeye.tests.support;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapFactory {
    public static <X extends Object, Y extends Object> Map<X, Y> newMap() {
        return new HashMap<X, Y>();
    }

    public static <X extends Object, Y extends Object> TreeMap<X, Y> newTreeMap() {
        return new TreeMap<X, Y>();
    }

    public static <X extends Object, Y extends Object> Map<X, Y> newOrderedMap() {
        return new LinkedHashMap<X, Y>();
    }

}
