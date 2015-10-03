package org.powlab.jeye.tests.assign;

public class AssignTest12a {

    public void test(int j) {
        int i = 1+(j < 3 ? (i=j+ (j<4 ? 4:2)) : (i=1));
    }
}
