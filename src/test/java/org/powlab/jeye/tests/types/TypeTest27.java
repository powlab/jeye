package org.powlab.jeye.tests.types;

public class TypeTest27 {

    void f(int x, short y) {
        System.out.println("1");
    }

    void f(short x, short y) {
        System.out.println("2");
    }

    void f(short x, int y) {
        System.out.println("3");
    }

    void f(int x, int y) {
        System.out.println("4");
    }

    void f(short x, int y, int z) {
        System.out.println("5");
    }

    void f(double x, int y, short z) {
        System.out.println("6");
    }

    void f(double x, int y, char z) {
        System.out.println("7");
    }

    void f(double x, int y, double z) {
        System.out.println("8");
    }

    void test(int i, short s, int i2, char c) {
        // 1
        f(i, s);
        // 1
        f((int) s, s);
        // 2 Изменения: '(short) s' -> 's'
        f((short) i, (short) s);
        // 4
        f(i, (int) s);
        // 7
        f((double) i, i, c);
        // 6
        f((double) i, i, (short) c);
        // 8 Изменения: '(int) s' -> '(double) s'
        f((double) i, i, (int) s);
        // 6 Изменения: 'c' -> '(int) c' (не очень)
        f((double) i, c, s);
        // 6
        f((double) i, (short) i, s);
    }

}
