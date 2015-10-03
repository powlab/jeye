package org.powlab.jeye.tests.switches;

public class SwitchTest1 {


    // LookupSwitch
    public void test1(int int0) {
        switch (int0) {
            case 1:
                System.out.println("One");   // Fall through
            case 3:
                System.out.println("Three");
                break;
            case 7:
                System.out.println("Seven");
                break;
            case 5000:
            case 11214:
                System.out.println("FiveK"); // Fall through
            default:
                System.out.println("Default");

        }
    }


    // Tableswitch
    public void test2(int int0) {
        switch (int0) {
            case 1:
                System.out.println("One");   // Fall through
            case 3:
                System.out.println("Three");
                break;
            case 6:
            case 7:
                System.out.println("Seven");
                break;
            case 5:
                System.out.println("Five"); // Fall through
            default:
                System.out.println("Default");

        }
    }

}
