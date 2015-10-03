package org.powlab.jeye.tests.control;

/**
 * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
 */
public class ControlFlowTest4 {
    public int foo(int i, int j) {
        while (true) {
            System.out.println("fred");
            try {
                while (i < j)
                    i = j++ / i;
            } catch (RuntimeException re) {
                i = 10;
                if (i > 5) continue;
            }
            break;
        }
        return j;
    }
}
