package org.powlab.jeye.tests.scope;

public class ScopeTest1 {
    private int x;

    public void foo(int y) {
        x = y;
    }

    public void foo2(int x) {
        this.x = x;
    }
}
