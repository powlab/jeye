package org.powlab.jeye.tests.control;

/**
 * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
 */
public class ControlFlowTest6 {
    public int foo(int i, int j) {
        while (true) {
            try {
                while (i < j) {
                    i = j++ / i;
                }
            } catch (RuntimeException re) {
                i = 10;
                continue;
            }
            System.out.println("Here.");
            if (i < 4) {
                break;
            }
        }
        return j;
    }
}
