package org.powlab.jeye.tests.constructor;

import java.io.Serializable;

public class ConstructorChainTest2 extends ConstructorChainTest1 implements Serializable, Cloneable {

    public ConstructorChainTest2(int x, int y) {
        super(x, y);
    }

    public ConstructorChainTest2() {
        super(3, 4);
    }

    public ConstructorChainTest2(int x) {
        this(3, 4);
    }

    @Override
    public int testFn() {
        return 12;
    }

    public int test1() {
        // generates invokevirtual
        return testFn();
    }

    public int test2() {
        // generates invokespecial
        return super.testFn();
    }

    private int testx() {
        return 3;
    }

    public int test3(ConstructorChainTest2 other) {
        // generates invokespecial
        return other.testx();
    }
}
