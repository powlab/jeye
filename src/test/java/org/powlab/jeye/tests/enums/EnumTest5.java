package org.powlab.jeye.tests.enums;



enum EnumTest5 {
    FOO{ public void f() { System.out.println("FOO!");} },
    BAR{},
    BAP,
    BOP{ public String toString() { return "OVERRIDE"; }};

    public void f() {
        System.out.println("DEFAULTF");
    }
}
