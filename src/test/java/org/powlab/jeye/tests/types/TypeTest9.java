package org.powlab.jeye.tests.types;

import java.util.List;

public class TypeTest9 {

    public void test2(boolean[] array0) {
        if (array0.length == 0) {
            System.out.println(array0);
        }
    }


    public void test3(List<Boolean> reference0) {
        if (reference0.isEmpty()) {
            System.out.println(reference0);
        }
    }
}
