package org.powlab.jeye.tests.constant;

public class ConstantFolding4 {
    public static void main(String[] argv) {
        double d = Double.MIN_VALUE;
        System.out.println("non strictfp : " + notStrictFP(d)); // may be 4.9E-324
        System.out.println("strictfp : " + strictFP(d)); // should be 0
    }

    static double notStrictFP(double a) {
        return a / 2.0 * 2.0;
    }

    static strictfp double strictFP(double a) {
        return a / 2.0 * 2.0;
    }
}
