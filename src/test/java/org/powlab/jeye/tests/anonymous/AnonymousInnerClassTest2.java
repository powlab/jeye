package org.powlab.jeye.tests.anonymous;


import org.powlab.jeye.tests.support.UnaryFunction;

public class AnonymousInnerClassTest2 {

    Integer invoker(int arg, UnaryFunction<Integer, Integer> fn) {
        return fn.invoke(arg);
    }

    public int doit(final int x, final int y) {
        return invoker(x, new UnaryFunction<Integer, Integer>() {

            @Override
            public Integer invoke(Integer arg) {
                return arg * hashCode() + x + y;
            }
        });
    }


}
