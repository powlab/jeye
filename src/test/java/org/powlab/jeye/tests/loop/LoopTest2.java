package org.powlab.jeye.tests.loop;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class LoopTest2 {


    public int testNested(List<Object> list, Set<Object> set) {
        int result = 0;
        for (Object j : list) {
            for (Object o : list) {
                for (Object o2 : set) {
                    if (o.equals(o2)) result++;
                }
            }
        }
        return result;
    }


}
