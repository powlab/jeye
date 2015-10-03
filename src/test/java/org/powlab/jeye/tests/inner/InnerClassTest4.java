package org.powlab.jeye.tests.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class InnerClassTest4 {

    public int getX() {
        return new Inner1(new ArrayList<String>()).getX(4);
    }

    public class Inner1 {
        private final List arg;

        public Inner1(List arg) {
            this.arg = arg;
        }

        public int getX(int y) {
            return 2;
        }

    }

    public class Inner2 extends Inner1 {
        public Inner2(List arg) {
            super(arg);
        }
    }

    public static class InnerBase {
        private final Set s;

        public InnerBase(Set s) {
            this.s = s;
        }
    }

    public class InnerDerived extends InnerBase {
        public InnerDerived(Set s) {
            super(s);
        }
    }
}
