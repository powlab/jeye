package org.powlab.jeye.tests.control;

///**
// * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
// */
public class ControlFlowTest2b {
    public int foo(int i, int j) {
        while (true) {
            try {
                // System.out.println("b");
                if (i++ < 5) {
                    System.out.println("a");
                } else if (i < 10) {
                    // System.out.println("F");
                    continue;
                } else {
                    continue;
                }
                // System.out.println("Fred");
                break;
            } catch (Exception e) {
                System.out.println("Except");
            }
        }
        return j;
    }
}
