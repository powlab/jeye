package org.powlab.jeye.tests.primitives;

/**
 * http://www.program-transformation.org/Transform/DecompilerControlFlowTestSource
 */
public class IncrTest1 {
    public int postincr(int i, int j) {
        j = i++ / 3;
        return j;
    }

    public int preincr(int i, int j) {
        j = ++i / 3;
        return j;
    }

    public int preincr2(int i, int j) {
        j = (i += 3) / 3;
        return j;
    }

}
