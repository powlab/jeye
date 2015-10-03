package org.powlab.jeye.tests.types;

import java.util.Map;

public class TypeTest4<T> {


    public void test1(Map<String, ?> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

    public void test2(Map<String, ? extends Object> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

    public void test3(Map<String, ? super Object> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

}
