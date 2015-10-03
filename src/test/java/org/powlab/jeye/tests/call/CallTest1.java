package org.powlab.jeye.tests.call;


public class CallTest1 {
    private static class A {
        void foo() {
            System.out.println("A");
        }
    }

    private static class B extends A {
        void foo() {
            System.out.println("B");
        }
    }

    public static void main(String [] args) {
        B b = new B();
        b.foo();
        ((A)b).foo();   // Obviously, calls virtual.... ;)

    }
}
