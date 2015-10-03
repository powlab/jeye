package org.powlab.jeye.tests.asserts;


import org.powlab.jeye.tests.support.MapFactory;

import java.util.Map;

public class AssertTest3 {

    private static class Inner {
        final static Map<String, String> map = MapFactory.newMap();
        final static Map<String, String> map2;

        static {
            map2 = MapFactory.newMap();
        }

        public void test1(String s) {
            assert (!s.equals("Fred"));
            System.out.println(s);
        }
    }
}
