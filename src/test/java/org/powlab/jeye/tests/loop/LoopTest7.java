package org.powlab.jeye.tests.loop;


public class LoopTest7 {

    char[] foo;

    public void test(int end) {
        char ch;
        int x = 0;
        int a, b;
        while ((ch = foo[x]) != '*') {
            System.out.println("" + x++ + ": " + ch);
        }
    }

}
