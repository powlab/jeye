package org.powlab.jeye.tests.member;

import java.util.HashMap;
import java.util.Map;

public class MemberTest3 extends MemberTest2 {

    private final int y = 2;
    private final int z;
    private int x = 3;
    private Map<String, String> m3 = new HashMap<String, String>(x);

    public MemberTest3() {
        this(6);
    }

    public MemberTest3(String s) {
        super(s);
        this.z = s.length();
    }

    public MemberTest3(int z) {
        this.z = z;
    }

    public int inc(int y) {
        return (x += y);
    }
}
