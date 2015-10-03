package org.powlab.jeye.tests.statics;


import java.util.HashMap;
import java.util.Map;

public class StaticInitTest2 {

    static final int x = 5;
    static final Map<String, String> map = new HashMap<String, String>(x);
}
