package org.powlab.jeye.tests.statics;

import java.util.HashMap;
import java.util.Map;

public class StaticInitTest3 {

    static int x = 5;
    static Map<String, String> map = new HashMap<String, String>(x);
}
