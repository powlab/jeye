package org.powlab.jeye.tests.if_empty;

import java.util.List;
import java.util.Set;


public class IfEmptyTest1 {

    /* 0_6 has a problem with empty blocks */

    public boolean test1(List<Object> list, Set<Object> set) {
        if (list == null) {
//            System.out.println("A");
            if (set == null) {
                System.out.println("B");
            } else {
            }
        } else if (set == null) {
//            System.out.println("D");
            if (list.isEmpty()) {
                System.out.println("E");
            } else {
            }
        }


        return true;
    }


}
