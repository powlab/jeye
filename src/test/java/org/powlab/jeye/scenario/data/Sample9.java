package org.powlab.jeye.scenario.data;

import java.util.Hashtable;

public class Sample9 {

    private Hashtable cflow = null;
    private int x1 = 0;
    private int x2 = 1;
    private int x3 = 2;
    private int x4 = 3;
    private int x5 = 4;
    private int x6 = 5;


    public Object[] any1(String reference0) {
        if (cflow == null) {
            cflow = new Hashtable();
        }
        return (Object[]) (Object[]) cflow.get(reference0);
    }

    public boolean any2(String reference0) {
        int int1 = x1 + x2 + x3 + x4 + x5;
        System.out.println(int1);
        return true;
    }

    public boolean any3(String reference0) {
        int int1 = (x1 + x2 + x3) * (x3 + x4 + x5);
        System.out.println(int1);
        return true;
    }

    public boolean any4(String reference0) {
        int int1 = x1  * (x3 + x4 + x5);
        System.out.println(int1);
        return true;
    }

    public char any5(String reference0) {
        return (char) ((x1 + x2 + x3) * x3);
    }

    public boolean any6(String reference0) {
        int int1 = (x1 + x3 - x2 + x3 - x4 * x5 - x6 / x3 / x2) * (x1 - x6);
        System.out.println(int1);
        return true;
    }

}
