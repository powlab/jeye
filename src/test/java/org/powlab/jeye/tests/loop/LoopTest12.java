package org.powlab.jeye.tests.loop;


public class LoopTest12 {


    public int test(int i, int j) {
        while (i < j)
            i = j++ / i;
        return 4;
    }


}
