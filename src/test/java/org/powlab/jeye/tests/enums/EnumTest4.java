package org.powlab.jeye.tests.enums;


enum EnumTest4 {
    FOO{ public void f() { System.out.println("FOO!");} },
    BAR{ public void f() { System.out.println("FOO TOO!");} };

    public abstract void f();
}
