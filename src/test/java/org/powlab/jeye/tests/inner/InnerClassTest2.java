package org.powlab.jeye.tests.inner;

import java.util.ArrayList;
import java.util.List;


public class InnerClassTest2 {

    @SuppressWarnings("unchecked")
    public int getX() {
        return new Inner1(new ArrayList<String>()).getX(4);
    }

    public class Inner1<E> {
        private final List<E> arg;

        public Inner1(List<E> arg) {
            this.arg = arg;
        }

        public int getX(int y) {
            return 2;
        }

    }
}
