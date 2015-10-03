package org.powlab.jeye.tests.assign;
// байт код теста такой же как и AssignTest14
public class AssignTest14b {

    public void test(int j) {
        int i = j++;
        System.out.println((i += ++j));
    }

}
