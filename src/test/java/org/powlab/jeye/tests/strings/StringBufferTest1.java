package org.powlab.jeye.tests.strings;

public class StringBufferTest1 {
    public String test1(int a, int b, int c) {
        return new StringBuffer().append("this ").append(a).append("is").append(b).append(" c? ").append(c).toString();
    }

    public String test2(int a, int b, int c) {
        return new StringBuffer("this ").append(a).append("is").append(b).append(" c? ").append(c).toString();
    }

    public String test3(int a, int b, int c) {
        return new StringBuilder().append("this ").append(a).append("is").append(b).append(" c? ").append(c).toString();
    }

    public String test4(int a, int b, int c) {
        return new StringBuilder("this ").append(a).append("is").append(b).append(" c? ").append(c).toString();
    }
}
