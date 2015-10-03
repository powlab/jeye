package org.powlab.jeye.tests.if_empty;

import java.util.List;
import java.util.Set;


public class IfEmptyTest4 {

    /* 0_6 has a problem with empty blocks */

    public boolean test1(List<Object> list, Set<Object> set) {
        if (list == null && set == null) {
        }


        return true;
    }


}
