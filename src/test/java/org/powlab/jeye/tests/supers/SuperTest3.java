package org.powlab.jeye.tests.supers;

import java.util.List;

public class SuperTest3 extends SuperBase {

    private void jim() {
    }

    public void test(List<SuperTest3> others) {
        for (SuperTest3 other : others) {
            other.jim();
        }
        SuperTest3 other = others.get(0);
        other.jim();
        jim();
        fred();
        super.fred();
    }

}
