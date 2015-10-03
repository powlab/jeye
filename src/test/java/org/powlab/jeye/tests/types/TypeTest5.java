package org.powlab.jeye.tests.types;

import java.util.Map;

public class TypeTest5<T> {


    public <Y extends String> void test1(Map<String, Y> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

    public <Y extends String> void test2(Map<String, ? extends Y> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

    public <Y extends String> void test3(Map<String, ? super Y> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

    public <Y extends String> void test4(Map<String, ? extends Y[]> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

    public <Y extends String> void test5(Map<String, ? extends String[]> reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }

}
