package org.powlab.jeye.tests.inner;

import java.util.ArrayList;
import java.util.List;


public class InnerClassTest8 {

    private int x = 3;

    public class Inner1 {

        public int tweakX(int y) {
            x += y;
            return x;
        }

    }
}
