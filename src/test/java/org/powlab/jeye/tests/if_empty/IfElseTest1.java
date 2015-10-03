package org.powlab.jeye.tests.if_empty;

import java.util.List;
import java.util.Set;


public class IfElseTest1 {


    public boolean test1(List<Object> list, Set<Object> set) {
        if (list == null) {
            if (set == null) {
                System.out.println("a");
            } else {
                System.out.println("b");
            }
        } else if (set == null) {
            System.out.println("c");
        } else if (list.isEmpty()) {
            if (set.isEmpty()) {
                System.out.println("d");
            } else {
                System.out.println("e");
            }
        } else {
            if (set.size() < list.size()) {
                System.out.println("f");
            } else {
                System.out.println("g");
            }
        }


        return true;
    }


}
