package org.powlab.jeye.tests.names;

public class NameTest13 {

    public int test(int x) {
        return x + 1;
    }

    public int foo(int i) {

        int a = test(1);
        a = test(a);
        ++a;
        ++a;
        ++a;
        ++a;
        a = test(a) + test(a);
        a = test(a) + test(a);
        a = test(a) + test(a);
        a = test(a) + test(a);
        ++a;
        ++a;
        a = ++a;
        a = a++;
        a = test(a);
        a = test(a);
        i = ++a;
        return a;
    }
}
