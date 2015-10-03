package org.powlab.jeye.tests.names;

public class NameTest3 {

    public void foo(int i) {
        {
            int a = 3;
            System.out.println(a);
        }
        String b = "5";
        {
            String a = "Hello";
            System.out.println(a + b);
        }
    }

}
