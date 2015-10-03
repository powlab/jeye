package org.powlab.jeye.tests.anonymous;

public class AnonymousInnerClassTest10 {
    final int k;

    AnonymousInnerClassTest10(final int k) {
        super();
        this.k = k;
    }

    public static void test() {
        final int n;

        final int value = new AnonymousInnerClassTest10(2 + (n = 3)) {
            int get() {
                return this.k + n;
            }
        }.get();

        if (value != 8) {
            throw new Error("value = " + value);
        }
    }
}
