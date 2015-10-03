package org.powlab.jeye.tests.inner;



public class InnerClassTest15b {
    private int a;

    public class Inner1 {
        private int b;

        public class Inner2 {
            public int c;

            public void foo() {
                System.out.println("Res" + (a+b+c));
                int c2 = Inner2.this.c;
                ++a;
                a+=2;
            }

        }

    }
}
