package org.powlab.jeye.tests.switches;

import org.powlab.jeye.tests.enums.EnumTest1;

public class SwitchTest2 {

    public int test0(EnumTest1 reference0) {
        switch (reference0) {
            case FOO:
                return 1;
            case BAP:
                return 2;
        }
        return 0;
    }

}
