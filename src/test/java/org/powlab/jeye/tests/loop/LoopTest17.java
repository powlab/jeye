package org.powlab.jeye.tests.loop;


public class LoopTest17 {

    char[] foo;

    public void test(int end) {
        int x = 3;
        do {
            System.out.println(x++);
            if (x > 5) break;
            System.out.println("fred");
        } while (true);
    }

}
