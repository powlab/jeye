package org.powlab.jeye.tests.loop;


public class LoopTest40d {

    int[] fn(int []i) {
        return i;
    }

    public void test(int [] xs)
    {
        for (int i : fn(xs)) {
            System.out.println(i);
        }
        fn(xs);
    }
}
