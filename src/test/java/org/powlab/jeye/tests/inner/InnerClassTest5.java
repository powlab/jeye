package org.powlab.jeye.tests.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class InnerClassTest5 {

    public int getX() {
        return new Inner1(new ArrayList<String>()).getX(4);
    }

    public static class Inner1 {
        private final List arg;

        public Inner1(List arg) {
            this.arg = arg;
        }

        public int getX(int y) {
            return 2;
        }

    }
}
