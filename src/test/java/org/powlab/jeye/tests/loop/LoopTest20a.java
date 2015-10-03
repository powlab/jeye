package org.powlab.jeye.tests.loop;


public class LoopTest20a {

    int y;

    public void test1(int [] as) {
        int $i = 93;
        int i$ = 94;
        for (int x : as) {
            System.out.println(i$);
            System.out.println($i + x);
        }
    }
}
