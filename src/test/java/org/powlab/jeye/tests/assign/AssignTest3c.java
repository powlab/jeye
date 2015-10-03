package org.powlab.jeye.tests.assign;

public class AssignTest3c {
    int x;
    int y;
    int z;

    void test3(int a) {
        x = a += 2;
        z = a;
    }
}
