package org.powlab.jeye.tests.anonymous;

public class AnonymousInnerClassTest9 {

    Integer invoker(int arg, Inner1 fn) {
        return fn.invoke(arg);
    }

    public int doit(final Integer x, Integer y) {
        return invoker(x, new Inner1() {

            @Override
            public Integer invoke(Integer arg) {
                return arg * 3 + x;
            }
        });
    }

    private interface Inner1 {

        public Integer invoke(Integer arg);
    }
}
