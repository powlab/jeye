package org.powlab.jeye.tests.loop;

import java.util.List;


public class LoopTestTyped4a {

    public void test5(List<String> lst) {

        List<String> lst2 = new Dup<List<String>>().dupIt(lst);
        for (String s : lst2) {
            System.out.println(s);
        }
    }

    public class Dup<E> {
        public E dupIt(E arg) {
            return arg;
        }
    }

}
