package org.powlab.jeye.tests.types;

public class TypeTest1a {


    public void test1() {
        int x = 3;
        char y = '3';
        short z = 3;  // unless you use localtype table, you'll get int.
        boolean a = true;
        String res = "Res : " + a + x + y+ z;
        System.out.println(res);
    }

}
