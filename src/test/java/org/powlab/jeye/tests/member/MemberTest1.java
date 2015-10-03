package org.powlab.jeye.tests.member;

public class MemberTest1 {

    private int x;

    public MemberTest1(int x) {
        this.x = x;
    }

    public int inc(int y) {
        return (x += y);
    }
}
