package org.powlab.jeye.tests.anonymous;

public class AnonymousInnerClassTest5 {

    private static abstract class UF {
        private final int x;

        public UF(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        abstract int invoke(int y);
    }

    static Integer invoke(int arg, UF fn) {
        return fn.invoke(arg);
    }

    public void main(String[] args) {
        final Integer z = Integer.parseInt(args[0]);
        System.out.println(invoke(4, new UF(3) {
            int invoke(int y) {
                return getX() + y + z;
            }
        }));
    }

}
