package org.powlab.jeye.tests.if_empty;

import java.util.List;
import java.util.Set;


public class IfEmptyTest5 {

    /* 0_6 has a problem with empty blocks */

    public boolean test1(List<Object> list, Set<Object> set) {
        if (list == null) {
            System.out.println("EEK");
        } else if (set == null) {
        }


        return true;
    }


}
