package org.powlab.jeye.tests.array;

public class ArrayTest13 {

    public void x(int int0, Integer ... array0) {
    }

    public void x(int int0, Integer reference0, int int1) {
    }

    public void t() {
        int array7[] = {1,2,3};
        int y[] = array7.length == 1 ? new int[] {2,3,4} : new int[]{1,2,3};
        y = new int[]{y[2],y[1],y[0]};
        y = y;


    }

}
