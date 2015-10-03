package org.powlab.jeye.tests.loop;

import java.util.List;


public class LoopTestTyped3 {

    public static <E> List<E> dup(List<E> arg) {
        return arg;
    }

    public void test5(List<String> lst) {

        List<String> lst2 = dup(lst);
        for (String s : lst2) {
            System.out.println(s);
        }
    }

}
