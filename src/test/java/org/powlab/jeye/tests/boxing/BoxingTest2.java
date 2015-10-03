package org.powlab.jeye.tests.boxing;

public class BoxingTest2 {
    private boolean t(int int0) {
        // be careful you don't unbox ;)
        return Integer.valueOf(int0) == null;
    }


}
