package org.powlab.jeye.tests.inner;

import java.util.ArrayList;
import java.util.List;


public class InnerClassTest6 {

    public int getX() {
        Inner1 a = new Inner1(3);
        return 2;
    }

    public class Inner1 {
        private final Inner1 rec;

        public Inner1(int x) {
            this.rec = x == 0 ? null : new Inner1(x - 1);
        }
    }

    public class Inner2 {
        private final Inner1 i1;

        public Inner2() {
            this.i1 = new Inner1(3);
        }
    }
}
