package org.powlab.jeye.tests.switches;

public class SwitchTest12 {

    public void test1(char char0, int int0) {
        switch (char0) {
            case 'a':
            case 'b':
            case 'c':
                System.out.println("a-c");
                break;
            case 'Z':
                do {
                    System.out.println("Z");
                } while (++int0 < 10);
                //break; не обязателен
        }
    }

}
