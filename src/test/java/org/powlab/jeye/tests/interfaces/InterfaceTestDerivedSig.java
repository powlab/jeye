package org.powlab.jeye.tests.interfaces;



import java.util.List;
import java.util.Map;

public interface InterfaceTestDerivedSig<A extends Integer> extends InterfaceTestBase, InterfaceTestBaseSig<String, A, Map<String, A>> {
    void test3(A a);

    void doit(A a, List<? super A> x);
}
