package org.powlab.jeye.tests.loop;


import java.util.ArrayList;
import java.util.List;

public class LoopTestTyped7a {

    public void foo() {
        List<String> lst = new ArrayList<String>();
        for (String s : lst) {
            System.out.println(s);
        }
    }

}
