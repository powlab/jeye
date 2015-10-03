package org.powlab.jeye.tests.loop;


public class LoopTest15 {

    char[] foo;

    public void test(int end) {
        char ch;
        int x = 0;
        while ((ch = foo[x + 1]) != '*') {
            System.out.println("" + x++ + ": " + ch);
        }
    }

}
