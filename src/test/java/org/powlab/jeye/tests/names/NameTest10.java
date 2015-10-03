package org.powlab.jeye.tests.names;

public class NameTest10 {

    public String mkString() {
        return "Hello";
    }

    public void foo(int i) {
        Object a = null;

        for (int x = 0; (x < 10 && a == null) || a != null; ++x) {
            a = 3;
            System.out.println(a);
            a = mkString();
        }

        a = "Hello";
        System.out.println(a);

    }
}
