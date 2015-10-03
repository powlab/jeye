package org.powlab.jeye.tests.primitives;


public class DoubleTest {

    public static double a() {
        double res = 0.0f;
        for (int x = 1; x <= 1000000; ++x) {
            res += 1.0f / (double) x;
        }
        return res;
    }

    public static double b() {
        double res = 0.0f;
        for (int x = 1000000; x >= 1; --x) {
            res += 1.0f / (double) x;
        }
        return res;
    }

    public static void main(String[] args) {
        // I've been using a() - can I replace it with b()?
        System.out.println(a());
        System.out.println(b());
    }
}
