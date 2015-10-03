package org.powlab.jeye.tests.boxing;

public class BoxingTest15a {
    private void test(Object reference0) {
        System.out.println("Object");
    }

    private void test(int int0) {
        System.out.println("int");
    }


    private void t(int int0) {
        test(Integer.valueOf(int0));
        //test((Object)Integer.valueOf(int0)); - same byte code
        test(int0);
    }

}
