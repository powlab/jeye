package org.powlab.jeye.tests.types;

import java.util.HashMap;
import java.util.Map;

public class TypeTest3<T> {


    public <X extends Map<String, Integer>, Y> void test1(X reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }


    public <X extends HashMap<String, Integer[][]>, Y> void test1(X reference0, T reference1) {
        StringBuilder reference3 = new StringBuilder();
        reference3.append(reference0);
    }
}
