package org.powlab.jeye.tests.inner;


import org.powlab.jeye.tests.support.UnaryFunction;


public class InnerClassSyntheticPrivateBridgeTest1 {

    Integer invoker(int arg, UnaryFunction<Integer, Integer> fn) {
        return fn.invoke(arg);
    }

    public int doit2(int x) {
        return invoker(x, new Fred());
    }

    private static class Fred implements UnaryFunction<Integer, Integer> {
        @Override
        public Integer invoke(Integer arg) {
            return arg * 3;
        }
    }
}
