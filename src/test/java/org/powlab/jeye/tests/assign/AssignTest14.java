package org.powlab.jeye.tests.assign;

public class AssignTest14 {

    public void test(int j) {
        int i = ++j;
        System.out.println(i+=++j);
    }
}
