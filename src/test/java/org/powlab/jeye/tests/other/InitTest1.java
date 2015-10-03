package org.powlab.jeye.tests.other;


import java.util.HashMap;
import java.util.Map;


public class InitTest1 {

    final int x = 5;
    final Map<String, String> map = new HashMap<String, String>(x);
    private final int y;

    public InitTest1() {
        y = 3;
    }

    public InitTest1(int y) {
        this.y = y;
    }
}
