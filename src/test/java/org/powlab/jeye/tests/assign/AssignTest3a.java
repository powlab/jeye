package org.powlab.jeye.tests.assign;

public class AssignTest3a {
    int x;
    int y;
    int z;

    void test3(int a) {
        x = ++a;
        z = a;
    }

    void test3b(int a) {
        x = a++;
        z = a;
    }
}
