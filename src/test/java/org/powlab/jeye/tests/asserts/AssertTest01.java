package org.powlab.jeye.tests.asserts;

import java.util.List;

public class AssertTest01 {

    public void test1(String s) {
        assert (!s.equals("Fred"));
        System.out.println(s);
    }
}
