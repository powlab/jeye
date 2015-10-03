package org.powlab.jeye.tests.anonymous;


import org.powlab.jeye.tests.support.UnaryFunction;

public class AnonymousInnerClassTest1 {

    Integer invoker(int arg, UnaryFunction<Integer, Integer> fn) {
        return fn.invoke(arg);
    }

    public int doit(int x) {
        return invoker(x, new UnaryFunction<Integer, Integer>() {
            @Override
            public Integer invoke(Integer arg) {
                return arg * 3;
            }
        });
    }

}
