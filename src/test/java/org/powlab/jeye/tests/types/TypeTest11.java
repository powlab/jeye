package org.powlab.jeye.tests.types;

import java.util.List;

public class TypeTest11 {

    public void test2(boolean boolean0) {
    }


    public void test3(List<Boolean> reference0) {
        boolean boolean0 = true;
        if (!boolean0) {
            test2(boolean0);
        }
    }
}
