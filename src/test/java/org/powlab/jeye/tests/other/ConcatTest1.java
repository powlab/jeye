package org.powlab.jeye.tests.other;


public class ConcatTest1 {
    public void test1(String a, String b, int c) {
        String res = "This " + a + " is a " + b + " stringbuilder desugaring test " + c;
        System.out.println(res);

    }
}
