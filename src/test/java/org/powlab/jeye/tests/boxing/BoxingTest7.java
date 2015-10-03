package org.powlab.jeye.tests.boxing;

public class BoxingTest7 {
    private boolean t(Integer reference0, int int0) {
        // If j is too big for the boxing cache, this will create a new object,
        // therefore the first test will FAIL even though i==j.
        // Unless it's incorrectly decompiled to i==j ;)
        return reference0 == Integer.valueOf(int0) && reference0 == int0;
    }


}
