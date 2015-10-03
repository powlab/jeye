package org.powlab.jeye.tests.constructor;

public class ThisConstructorTest {

    private final int x;
    private final String y;

    public ThisConstructorTest(int x, String y) {
        this.x = x;
        this.y = y;
    }

    public ThisConstructorTest(int x) {
        this(x, "Null");
    }

    public ThisConstructorTest() {
        this(3);
    }
}
