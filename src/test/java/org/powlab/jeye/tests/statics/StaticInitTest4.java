package org.powlab.jeye.tests.statics;

import java.util.HashMap;
import java.util.Map;

public class StaticInitTest4 {

    static int x = 5;
    static Map<String, String> map;

    static {
        map = new HashMap<String, String>(x);
    }
}
