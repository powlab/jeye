package org.powlab.jeye.tests.other;

import java.util.List;
import java.util.Set;


public class GotoTest1 {


    public boolean test (boolean a, boolean b) {
        c: break c;
        d: break d;
        e: break e;
        f: break f;
        b: break b;
        a: return b;
    }

}
