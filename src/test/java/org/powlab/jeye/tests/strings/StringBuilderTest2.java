package org.powlab.jeye.tests.strings;

public class StringBuilderTest2 {
    public String test1(int a, int b, int c) {
        return "this " + a + b + "is" + (a + b - c) + " c? " + (a * b - c) + " c? " + (a + b - c);
    }

    public static void main(String[] args) {
        System.out.println(new StringBuilderTest2().test1(1, 20, 300));
    }
}
