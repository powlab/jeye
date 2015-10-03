package org.powlab.jeye.tests.trycf;


public class DynamicCatchTest1 {
    int x;
    int y;
    int z;

    private static void rethrow(Throwable t) throws Throwable {
        throw t;
    }

    static void test1(int a, int b) {
        try {
            try {
                throw new UnsupportedOperationException();
            } catch (RuntimeException e) {
                rethrow(e);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("Dynamic");
        } catch (Throwable e) {
            System.out.println("Static");
        }
    }

    public static void main(String[] args) {
        test1(2, 3);
    }
}
