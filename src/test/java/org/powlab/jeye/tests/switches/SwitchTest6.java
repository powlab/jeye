package org.powlab.jeye.tests.switches;

public class SwitchTest6 {

    enum enm {
        ONE,
        TWO,
        THREE,
        WIBBLE
    }

    ;

    public int test0(enm e) {
        switch (e) {
            case ONE:
                return 1;
            case THREE:
            case WIBBLE:
                System.out.println("Fallthrough!");
            case TWO:
                return 2;
        }
        return 0;
    }

}
