package org.powlab.jeye.tests.loop;


public class LoopTest14 {

    char[] foo;

    public void test(int end) {
        char ch;
        int x = 0;
        while ((ch = ((x % 2 == 0) ? foo[x + 1] : foo[x])) != '*') {
            System.out.println("" + x++ + ": " + ch);
        }
    }

    public boolean test1() {
        return true;
    }

    public void test2() {
        Object o = (Object) test1();
        System.out.println(o + " " + o.getClass());
    }

    public static void main(String args[]) {
        LoopTest14 l = new LoopTest14();
        l.test2();
    }

}
