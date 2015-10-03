package org.powlab.jeye.tests.constructor;


public class ConstructorChainTest3 extends ConstructorChainTest1 {

    public ConstructorChainTest3(int x, int y) {
        super(x, y);
    }

    public ConstructorChainTest3(int x) {
        this(3, 4);
        new ConstructorChainTest3(3,4);
    }
}
