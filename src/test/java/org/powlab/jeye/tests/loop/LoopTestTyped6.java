package org.powlab.jeye.tests.loop;


import java.util.ArrayList;
import java.util.List;

public class LoopTestTyped6 {

    private final List<Boolean> lst = new ArrayList<Boolean>();

    public List<Boolean> foo() {
        for (Boolean b : lst) {
            System.out.println(b);
        }
        return lst;
    }

}
