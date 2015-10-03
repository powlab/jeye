package org.powlab.jeye.tests.names;

public class NameTest7 {

    public void foo(int i) {
        Object a;
        {
            a = 3;
            System.out.println(a);
        }
        {
            if (i == 3) {
                a = "Hello";
            }
            System.out.println(a);
        }
        System.out.println(a);
    }
}
