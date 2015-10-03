package org.powlab.jeye.tests.names;

public class NameTest15 {

    public int foo(Goto f) {
        f.foo();
        return 1;
    }

    private static class Goto {
        void foo(){
        }
    }

}
