package org.powlab.jeye.tests.names;

public class NameTest2 {

    public void foo(int i) {
        {
            int a = 3;
            System.out.println(a);
        }
        {
            int a = 4;
            System.out.println(a);
        }
    }
}
