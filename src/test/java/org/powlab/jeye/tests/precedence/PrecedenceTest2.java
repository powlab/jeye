package org.powlab.jeye.tests.precedence;

public class PrecedenceTest2 {
    public int test1(int a, int b, int c) {
        return a+b*c;
    }

    public int test2(int a, int b, int c) {
        return (a+b)*c;
    }
}
