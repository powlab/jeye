package org.powlab.jeye.tests.switches;


public class EnumSwitchTest1 {

    enum enm {
        ONE,
        TWO,
        THREE,
        FOUR
    }

    public int test0(enm e) {
        switch (e) {
            default:
                System.out.println("Test");
                break;
            case ONE:
                return 9;
            case TWO:
                return 2;
        }
        System.out.println("Here");
        return 1;
    }


}
