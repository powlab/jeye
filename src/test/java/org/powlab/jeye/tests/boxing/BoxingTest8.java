package org.powlab.jeye.tests.boxing;

public class BoxingTest8 {
    private boolean t(Integer reference0, int int0, Integer reference1) {
        switch (reference0) {
            case 1:
                break;
            case 2:
            case 3:
                System.out.println(reference1);
            case 4:
                System.out.println(reference0.hashCode());
            default:
                reference1 = reference0;
        }
        System.out.println("X");
        return false;
    }


}
