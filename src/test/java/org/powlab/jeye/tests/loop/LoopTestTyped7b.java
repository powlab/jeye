package org.powlab.jeye.tests.loop;


import java.util.ArrayList;
import java.util.List;

public class LoopTestTyped7b {

    public void foo() {
        List<Object> lst = new ArrayList<Object>();
        for (Object s : lst) {
            System.out.println((String)s);
        }
    }

}
