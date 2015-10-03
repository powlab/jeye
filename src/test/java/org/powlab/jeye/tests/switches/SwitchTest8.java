package org.powlab.jeye.tests.switches;

public class SwitchTest8 {
    private int foo(boolean a, boolean b) {
        return a ? 1 : b ? 2 : 3;
    }

    public void test1(int a, int b) {
        switch (foo(a == 3, b == 12)) {
            case 0:
                System.out.println("0");
            case 1:
                System.out.println("1");
                break;
            case 2:
                System.out.println("2");
            //default: не обязательно
        }
        System.out.println("Test");
    }

}
