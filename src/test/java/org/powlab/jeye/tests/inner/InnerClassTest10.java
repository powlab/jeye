package org.powlab.jeye.tests.inner;


import org.powlab.jeye.tests.support.SetFactory;


public class InnerClassTest10 {

    public class Inner1 {

        public InnerClassTest4.InnerBase tweak(int y) {
            return new InnerClassTest4.InnerBase(SetFactory.newSet(y));
        }

    }
}
