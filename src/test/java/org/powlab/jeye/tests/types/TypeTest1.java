package org.powlab.jeye.tests.types;

public class TypeTest1 {


    public void test1() {
        StringBuilder reference1 = new StringBuilder();
        char char0 = 'a';
        reference1.append(char0);
        reference1.append((int) char0);
    }

    public void test2() {
        StringBuilder reference1 = new StringBuilder();
        char char0 = 'a';
        reference1.append((int) char0);
        reference1.append(char0);
    }

    public void test3() {
        StringBuilder reference1 = new StringBuilder();
        int int1 = 97;
        reference1.append(int1);
        reference1.append((char) int1);
    }

    public void test4() {
        StringBuilder reference1 = new StringBuilder();
        int int1 = 97;
        reference1.append((char) int1);
        reference1.append(int1);
    }
}
