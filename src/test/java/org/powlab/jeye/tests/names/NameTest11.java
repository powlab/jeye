package org.powlab.jeye.tests.names;

public class NameTest11 {

    public int test(int x) {
        return x;
    }

    public int foo(int i) {

        int a = test(1);
        a = test(a);
        ++a;
        ++a;
        ++a;
        ++a;
        ++a;
        ++a;
        a = test(a);
        a = test(a);
        System.out.println("a=" + a);
        return a;
    }
}
