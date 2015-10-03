package org.powlab.jeye.tests.args;

public class ArgClassifyTest1 {

    void test1(String a) {
        System.out.println("a");
    }

    void test1(Object a) {
        System.out.println("b");
    }

    public static void main(String[] args) {
        String a = "fdfd";
        ArgClassifyTest1 ar = new ArgClassifyTest1();
        ar.test1(a);
        Object b = a;
        ar.test1(b);
    }
}
