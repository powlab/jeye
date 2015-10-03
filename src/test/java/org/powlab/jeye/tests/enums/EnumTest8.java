package org.powlab.jeye.tests.enums;



enum EnumTest8 {
    FOO("YAY!"){ public void f() { System.out.println(x);} },
    BAR("f"){  },
    BAP("BAP"),
    BOP("F"){ public String toString() { return "OVERRIDE" + FOO.name(); }};
    private static String x = "BAR";

    private final String s;

    private EnumTest8(String s) {
        this.s = s;
    }

    public void f() {
        System.out.println("DEFAULTF");
    }
}
