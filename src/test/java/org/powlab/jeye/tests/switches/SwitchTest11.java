package org.powlab.jeye.tests.switches;

public class SwitchTest11 {

    public void test1(char char0, int int0) {
        switch (char0) {
            case 'a':
            case 'b':
            case 'c':
                System.out.println("a-c");
                break;
            case 'Z':
                while (int0 < 10) {
                    System.out.println("Z");
                    ++int0;
                }
        }
    }

}
