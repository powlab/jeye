package org.powlab.jeye.tests.constructor;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ConstructorTest5 {

    private final Map<String, String> fred = new HashMap();

    public ConstructorTest5(int x) {
        System.out.println("freD");
    }
}
