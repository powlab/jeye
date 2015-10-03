package org.powlab.jeye.tests.names;

public class NameTest9 {

    public String mkString() {
        return "Hello";
    }

    public void foo(int i) {
        Object a = null;

        for (int x = 0; (x < 12 && a == null) || a != null; ++x) {
            a = 3;
            System.out.println(a);
            if (x == 0) {
                a = mkString();
            }
        }
        a = "Hello";
        System.out.println(a);

    }

}
