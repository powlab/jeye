package org.powlab.jeye.tests.names;

public class NameTest16 {

    public int foo(Goto f) {
        Goto g = new Goto();
        f.foo();
        g.foo();
        return 1;
    }

    public static class Goto {
        void foo(){
        }
    }

}
