package org.powlab.jeye.tests.primitives;

public class IntTest1 {

    public int plus1(int int0) {
        return int0 + 1;
    }

    public int func1(int int0, int int1) {
        return int0 + (int1 - 1) * (int0 + 1);
    }

    public int func2(int int0, int int1) {
        return int0 + (int0 > int1 ? int0 + 1 : int1 - 1);
    }
}
