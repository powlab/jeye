package org.powlab.jeye.tests.loop;

import java.util.List;

public class LoopTestTyped4b {

    public void test5(List<String> lst) {

        List<String> lst2 = new Dup<List<String>>().dupIt(lst);
    }

    public class Dup<E> {
        public E dupIt(E arg) {
            return arg;
        }
    }

}
