package org.powlab.jeye.tests.loop;


public class LoopTest8 {

    char[] foo;

    public void test(int end) {
        char ch;
        int x = 0;
        while ((ch = foo[++x]) != '*') {
            System.out.println("" + x + ": " + ch);
        }
    }

}
