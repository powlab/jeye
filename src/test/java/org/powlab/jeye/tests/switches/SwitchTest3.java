package org.powlab.jeye.tests.switches;

public class SwitchTest3 {

    enum enm {
        ONE,
        TWO,
        THREE,
        FOUR
    }

    ;

    public int test0(enm e) {
        fred:
        for (int x = 0; x < 10; ++x) {
            switch (e) {
                default:
                    System.out.println("Test");
                    break;
                case ONE:
                    return 1;
                case TWO:
                    return 2;
                case FOUR:
                    break fred;
                case THREE:
                    break fred;
            }
            System.out.println("Here");
        }
        return 1;
    }


}
