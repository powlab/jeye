package org.powlab.jeye.tests.control;

///**
// * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
// */
public class ControlFlowTest4a {
    public int foo(int i, int j) {
        while (true) {
            System.out.println("fred");
            try {
                while (i < j) {
                    i = j++ / i;
                }
            } catch (RuntimeException re) {
                if (i < 2) {
                    i = i + 1;
                }
                if (i > 5) continue;
            }
            break;
        }
        return j;
    }
}
