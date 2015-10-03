package org.powlab.jeye.tests.precedence;

public class PrecedenceTest7 {

    public int test1(Object fred, int x) {
        return ((String[]) fred)[x].length();
    }
}
