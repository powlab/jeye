package org.powlab.jeye.tests.boxing;

public class BoxingTest5 {
    private boolean t(Integer reference0) {
        try {
            if (Integer.valueOf(reference0) == null) {
                return false;
            }
            return false;
        } catch (NullPointerException reference2) {
            return true;
        }
    }


}
