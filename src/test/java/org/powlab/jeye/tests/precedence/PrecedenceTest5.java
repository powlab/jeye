package org.powlab.jeye.tests.precedence;

public class PrecedenceTest5 {

    public int test1(boolean a, int b, int c) {
        return a ? b + c : b * c;
    }
}
