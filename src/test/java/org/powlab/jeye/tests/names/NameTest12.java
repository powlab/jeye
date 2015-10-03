package org.powlab.jeye.tests.names;

public class NameTest12 {

    public int test(int x) {
        return x;
    }

    public int foo(int i) {

        int a = test(i);
        a = test(a);
        ++a;
        ++a;
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 12; ++y) {
                ++a;
                ++a;
            }
        }
        ++a;
        ++a;
        a = test(a);
        a = test(a);
        System.out.println("a=" + a);
        return a;
    }
}
