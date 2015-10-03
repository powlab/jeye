package org.powlab.jeye.tests.assign;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class AssignTest8 {

    public void test() {
        ArrayList l = new ArrayList();
        ((List)l).size();
        ((ArrayList)l).size();
        ((AbstractList)l).size();
    }
}
