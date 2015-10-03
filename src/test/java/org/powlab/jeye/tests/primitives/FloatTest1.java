package org.powlab.jeye.tests.primitives;


public class FloatTest1 {

    public static float a() {
        float res = 0.0f;
        for (int x = 1; x <= 1000000; ++x) {
            res += 1.0f / (float) x;
        }
        return res;
    }

    public static float b() {
        float res = 0.0f;
        for (int x = 1000000; x >= 1; --x) {
            res += 1.0f / (float) x;
        }
        return res;
    }

    public static void main(String[] args) {
        // I've been using a() - can I replace it with b()?
        System.out.println(a());
        System.out.println(b());
    }
}
