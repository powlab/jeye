package org.powlab.jeye.tests.types;

public class TypeTest7 {

    // Test 2 and test 3 produce EXACTLY the same bytecode
    public int test2(int int0) {
        boolean boolean0 = false;
        if (int0 > 4) {
            boolean0 = true;
        }
        return boolean0 ? 5 : 2;
    }

    // ....
    public int test3(int int0) {
        int int2 = 0;
        if (int0 > 4) {
            int0 = 1;
        }
        return int2 != 0 ? 5 : 2;
    }

    public int test4(int int0) {
        int int2 = 0;
        if (int0 > 4) {
            int2 = 2;
        }
        return int2 != 0 ? 5 : 2;
    }

}

