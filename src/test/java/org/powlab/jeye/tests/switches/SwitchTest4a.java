package org.powlab.jeye.tests.switches;

public class SwitchTest4a {

    // Disabled because I don't want to make this java7 yet!

    public int test0(String reference0) {
        switch (reference0) {
            default:
                System.out.println("Test");
                break;
            case "FRED":
                return 1;
            case "JIM":
                return 2;
            case "BB":  // BB and Aa have the same hashcode.  So how do we do the negative test?
            case "Aa":
                return 13;
        }
        System.out.println("Here");
        return 0;
    }


}
