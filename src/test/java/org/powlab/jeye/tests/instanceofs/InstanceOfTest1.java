package org.powlab.jeye.tests.instanceofs;

/**
 * User: lee
 * Date: 11/2013
 */
public class InstanceOfTest1 {

    public static class Integer {
    }

    public int test(Object i) {
        if (i instanceof Integer) {
            return 1;
        }
        if (i instanceof java.lang.Integer) {
            return 2;
        }
        System.out.println("0");
        return 0;
    }
}
