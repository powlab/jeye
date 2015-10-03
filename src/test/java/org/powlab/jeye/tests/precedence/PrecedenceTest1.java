package org.powlab.jeye.tests.precedence;

public class PrecedenceTest1 {
    public int test1() {
        return new String("fred").hashCode();
    }

    public int test2() {
        return (new String("fred")).hashCode();
    }
}
