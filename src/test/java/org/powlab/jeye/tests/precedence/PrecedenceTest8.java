package org.powlab.jeye.tests.precedence;

public class PrecedenceTest8 {

    public int test1(Object[] fred, int x) {
        return ((Inner) fred[x]).value;
    }

    public class Inner {
        public int value;
    }
}
