package org.powlab.jeye.tests.scope;

import org.powlab.jeye.tests.support.BinaryFunction;

public class ScopeTest5 {
    private BinaryFunction<Integer, Double, Double> func;
    private static BinaryFunction<Integer, Double, Double> func2;

    private class Inner {
        private int x;

        public Inner() {
            System.out.println(func.invoke(x,4.2).toString());
            System.out.println(func2.invoke(x,4.2).toString());
        }
    }
}
