package org.powlab.jeye.tests.enums;


import org.powlab.jeye.tests.support.MapFactory;

import java.util.Map;


public enum EnumTest3 {
    FOO(2),
    BAR(1),
    BAP(5);

    private final int x;
    private final static Map<String, EnumTest3> smap = MapFactory.newMap();
    private final static EnumTest3 tmp = BAP;

    private EnumTest3(int x) {
        this.x = x;
    }

    public static void main(String args[]) {
        System.out.println(FOO);
    }
}
