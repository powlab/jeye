package org.powlab.jeye.tests.inner;


public class InnerClassTest12 {
    public int a;

    public class Inner1 {
        public int b;

        public class Inner2 {
            public int c;

            public void foo() {
                System.out.println("Res" + (a+b+c));
                int c2 = Inner2.this.c;
                int b2 = Inner1.this.b;
                int a2 = InnerClassTest12.this.a;
            }

        }

    }
}
