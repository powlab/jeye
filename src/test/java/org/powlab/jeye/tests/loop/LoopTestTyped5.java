package org.powlab.jeye.tests.loop;


import java.util.List;

import org.powlab.jeye.tests.support.Functional;
import org.powlab.jeye.tests.support.Predicate;

public class LoopTestTyped5 {

    public static List<Boolean> foo(List<Boolean> fooIn) {
        List<Boolean> res = Functional.filter(fooIn, new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean in) {
                return true;
            }
        });
        for (Boolean b : res) {
            System.out.println(b);
        }
        return res;
    }

}
