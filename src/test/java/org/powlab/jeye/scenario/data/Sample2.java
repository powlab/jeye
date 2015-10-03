package org.powlab.jeye.scenario.data;

public class Sample2 {

    public boolean switch1(Integer value, int k) {
        switch (k) {
            case 0:
                value = null;
        }
        return false;
    }

    public boolean switch2(Integer value, int k) {
        switch (k) {
            case 0:
                value = null;
            case 1:
                k++;
                break;
            case 2:
                k++;
                return true;
            default:
                k = 9;

        }
        return false;
    }

    public boolean switch3(Integer value, int k) {
        switch (k) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                value = null;
        }
        return false;
    }

    public boolean switch4(Integer value, int k) {
        switch (k) {
            case 4:
                k--;
            case 9:
                k++;
        }
        return false;
    }

    public boolean switch5(Integer value, int k) {
        switch (k) {
            case 4:
            default:
                k--;
            case 9:
                k++;
        }
        return false;
    }

    public boolean switch6(Integer value, int k) {
        switch (k) {
            case 4:
            default:
                k--;
                break;
            case 9:
                k++;
        }
        return false;
    }

    public boolean switch7(Integer value, int k) {
        switch(k) {
            case 4:
                if (value == null) {
                    break;
                }
                k++;
                break;
            default: k--;
        }
        return false;
    }

    public boolean switch8(Integer value, int k) {
        switch(k) {
            case 4:
                if (value == null) {
                    k += 2;
                } else {
                    k += 5;
                }
                k++;
                break;
            default: k--;
        }
        return false;
    }

    public int switch9(String reference0) {
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

    public int switch0(String reference0) {
        switch (reference0) {
            default:
                System.out.println("Test");
                break;
            case "A":
                return 12;
            case "B":
            case "C":
                return 13;
        }
        System.out.println("Here");
        return 0;
    }

    public void switchq(int int0) {
        switch (int0) {
            case 0:
                System.out.println("0");
            case 1:
                System.out.println("1");
                break;
            case 2:
                System.out.println("2");
        }
        System.out.println("Test");
    }

    public void switchw(int e) {
        synchronized (this) {
            int x = 3;
        }
    }

}
