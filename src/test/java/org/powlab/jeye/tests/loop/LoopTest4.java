package org.powlab.jeye.tests.loop;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class LoopTest4 {


    public void test5(int end) {
        int x = 0;
        do {
            if (x < end) continue;
            System.out.println(x);
        } while ((x = x + 2) < end);
    }


}
