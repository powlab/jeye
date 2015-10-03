package org.powlab.jeye.tests.loop;

public class LoopTest34 {

    public void cannotMoveIteratorVariableDeclaration(Integer[] l2, Integer[] l3) {
        for (Integer i : l2) {
            i = l3[i];
        }
    }
}
