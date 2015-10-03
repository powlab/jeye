package org.powlab.jeye.tests.enums;



enum EnumTest6 {
    FOO{ public void f() { System.out.println(x);} },
    BAR{},
    BAP,
    BOP{ public String toString() { return "OVERRIDE"; }};
    private static String x = "BAR";

    public void f() {
        System.out.println("DEFAULTF");
    }
}
