package org.powlab.jeye.tests.control;

///**
// * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
// */
public class ControlFlowTest3 {
    public int foo(int i, int j) {
        while (true) {
            System.out.println("b");
            if (i++ < 5) {
                System.out.println("a");
            } else if (i < 10) {
                System.out.println("F");
                continue;
            } else {
                continue;
            }
            System.out.println("Fred");
            break;
        }
        return j;
    }
}
