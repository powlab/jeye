package org.powlab.jeye.tests.types;

public class TypeTest8 {

    // Test 2 and test 3 produce EXACTLY the same bytecode
    public void test2(int int0) {
        for (boolean boolean1 : new boolean[]{true, false}) {
            System.out.println(boolean1);
        }
    }

    // ....
    public void test3(int int0) {
        for (int int6 : new int[]{1, 0}) {
            System.out.println(int6);
        }
    }
}
