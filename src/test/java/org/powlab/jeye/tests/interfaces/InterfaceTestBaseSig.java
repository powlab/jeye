package org.powlab.jeye.tests.interfaces;



import java.util.Map;

public interface InterfaceTestBaseSig<A, B, T extends Map<A, B>> {
    T test2(T arg);
}
