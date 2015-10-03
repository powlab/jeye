package org.powlab.jeye.tests.types;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeTest33<E> {
    private transient Map<E, Object> map;

    public TypeTest33() {
        map = new HashMap<E, Object>();
    }

    public TypeTest33(int x) {
        map = new LinkedHashMap<E, Object>();
    }
}
