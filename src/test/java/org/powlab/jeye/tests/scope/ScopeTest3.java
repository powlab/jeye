package org.powlab.jeye.tests.scope;

public class ScopeTest3 {
    int x;

    public void foo(int y) {
        x = y;
    }

    public void foo2(int x) {
        this.x = x;
    }

    private class Inner {

        public Inner() {
            System.out.println(x);
        }
    }
}
