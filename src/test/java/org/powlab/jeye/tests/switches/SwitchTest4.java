package org.powlab.jeye.tests.switches;

public class SwitchTest4 {

    // Disabled because I don't want to make this java7 yet!

    public int test0(String reference0) {
        switch (reference0) {
            default:
                System.out.println("Test");
                break;
            case "BB":  // BB and Aa have the same hashcode.
                return 12;
            case "Aa":
            case "FRED":
                return 13;
        }
        System.out.println("Here");
        return 0;
    }


}
