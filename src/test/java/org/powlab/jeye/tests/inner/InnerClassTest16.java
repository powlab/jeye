package org.powlab.jeye.tests.inner;



public class InnerClassTest16 {
    private int[] a;

    public class Inner1 {
        private int b;

        public class Inner2 {
            public int c;

            public void foo() {
                System.out.println("Res" + (a[2] + b));
            }

        }

    }
}
