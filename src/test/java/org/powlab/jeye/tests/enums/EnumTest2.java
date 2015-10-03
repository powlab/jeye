package org.powlab.jeye.tests.enums;


import org.powlab.jeye.tests.support.MapFactory;

import java.util.Map;


public enum EnumTest2 {
    FOO(2),
    BAR(1),
    BAP(5);

    private final int x;
    private final static Map<String, EnumTest2> smap = MapFactory.newMap();

    private EnumTest2(int x) {
        this.x = x;
    }
}
