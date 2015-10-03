package org.powlab.jeye.tests.loop;


import java.util.ArrayList;
import java.util.List;


public class LoopTestTyped1 {

    public void test5(String a, String b) {
        List<String> lst = new ArrayList<String>();
        lst.add(a);
        lst.add(b);

        for (String s : lst) {
            System.out.println(s);
        }
    }

}
