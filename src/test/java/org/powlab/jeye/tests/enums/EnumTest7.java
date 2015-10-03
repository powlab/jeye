package org.powlab.jeye.tests.enums;



enum EnumTest7 {
    FOO{ public void f() { System.out.println(x);} },
    BAR{},
    BAP,
    BOP{ public String toString() { return "OVERRIDE" + FOO.name(); }};
    private static String x = "BAR";

    public void f() {
        System.out.println("DEFAULTF");
    }
}
