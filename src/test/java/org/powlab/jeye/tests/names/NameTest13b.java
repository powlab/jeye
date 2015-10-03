package org.powlab.jeye.tests.names;

public class NameTest13b {

    public int foo() {
        int a = 1;
        a = ++a;
        a = a++;
        System.out.println(a);
        return a;
    }
}
