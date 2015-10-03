package org.powlab.jeye.tests.control;

///**
// * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
// */
public class ControlFlowTest8 {
    public int foo(int i, int j) {
        do {
            i++;
            continue;
        } while (i < j);
        return j;
    }
}
