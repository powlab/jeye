package org.powlab.jeye.tests.trycf;

import java.util.List;

public class TryTest3 {


    public void test1(int x, List<Integer> a) {
        for (Integer y : a) {
            try {
                if (x < 3) {
                    System.out.println("a");
                } else {
                    System.out.println("b");
                }
                //continue; не обязательно
            } catch (RuntimeException e) {
                System.out.println("e");
            }
        }
        System.out.print(5);
    }
}
