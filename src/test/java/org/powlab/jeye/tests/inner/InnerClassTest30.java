package org.powlab.jeye.tests.inner;


public class InnerClassTest30 {

    public static interface I {
        Object get(Object o);
    }

    public static class C implements I {
        /*
         * Override with covariant return type.
         */
        @Override
        public Double get(Object o) {
            return 2.0;
        }
    }

}
