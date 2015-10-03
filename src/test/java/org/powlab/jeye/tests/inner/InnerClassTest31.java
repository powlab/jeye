package org.powlab.jeye.tests.inner;


public class InnerClassTest31 {

    public int foo() {
        class Bob {
            public Bob mkBok() {
                return new Bob();
            }

            int get() {
                return 1;
            }
        }

        return new Bob().mkBok().get();
    }

}
