package org.powlab.jeye.tests.member;


import java.util.HashMap;
import java.util.Map;

public class MemberTest2 {

    private final int y = 2;
    private final int z;
    private int x = 3;
    private Map<String, String> m = new HashMap<String, String>(x);

    public MemberTest2() {
        this(6);
    }

    public MemberTest2(String s) {
        this.z = s.length();
    }

    public MemberTest2(int z) {
        this.z = z;
    }

    public int inc(int y) {
        return (x += y);
    }
}
