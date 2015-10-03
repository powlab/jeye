package org.powlab.jeye.tests.iters;

import java.util.List;
import java.util.Set;


public class IterTest5 {


    public boolean test1(List<String> list, Set<Integer> set) {
        boolean result = false;
        int x = 3;
        int y = 1; // you can't tell this is an int.
        for (x = 1; x < 12; ++x) {    // use of an itervar already in scope.
            System.out.println(x);
        }
        return result;
    }
}
