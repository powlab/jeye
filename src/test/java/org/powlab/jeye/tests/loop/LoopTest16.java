package org.powlab.jeye.tests.loop;


public class LoopTest16 {

    char[] foo;

    public void test(int end) {
        char ch;
        for (int x = 0, y = 0; x < 10; x++, y += 2) {
            System.out.println("x" + x + "y" + y);
        }
    }

}
