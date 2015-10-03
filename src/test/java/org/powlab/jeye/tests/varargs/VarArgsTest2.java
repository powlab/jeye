package org.powlab.jeye.tests.varargs;

public class VarArgsTest2 {
    public String test(final char c) {
        return String.format("'\\u%1$04x'", (int) c);
    }
}
