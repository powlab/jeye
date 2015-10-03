package org.powlab.jeye.tests.loop;


import java.util.ArrayList;
import java.util.List;

public class LoopTestTyped7c {

    public void foo() {
        List lst = new ArrayList();
        for (Object s : lst) {
            System.out.println((String)s);
        }
    }

}
