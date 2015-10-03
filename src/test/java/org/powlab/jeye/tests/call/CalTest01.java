package org.powlab.jeye.tests.call;

import java.util.Calendar;

public class CalTest01 {

    public void test1(String s) {
        System.out.println(Calendar.getInstance().getTimeZone().getID());
    }

    public static void main(String[] args) {
        new CalTest01().test1(args[0]);
    }
}
