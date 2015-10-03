package org.powlab.jeye.tests.names;

public class NameTest6 {

    public void foo(int i) {
        Object a;
        {
            a = 3;
            System.out.println(a);
        }
        {
            a = "Hello";
            System.out.println(a);
        }
    }

}
