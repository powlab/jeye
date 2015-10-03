package org.powlab.jeye.tests.assign;

public class AssignTest12 {

    public void test(int j) {
        int i = 1 + (j < 3 ? (i = j + (j < 4 ? (i = 4) : (i = 2))) : (i = 1));
    }
}
