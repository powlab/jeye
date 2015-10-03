package org.powlab.jeye.tests.primitives;

/**
 * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
 */
public class IncrTest2 {
    public int postincr(int i, int j) {
        return ++j;
    }

    public int preincr(int i, int j) {
        return j++;
    }

}
