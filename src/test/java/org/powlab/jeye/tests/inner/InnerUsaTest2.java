package org.powlab.jeye.tests.inner;


public class InnerUsaTest2 {
    public String name = "Detroit";

    public class England {
        public String name = "London";

        public class Ireland {
            public String name = "Dublin";

            public void print_names() {
                System.out.println(name);
                System.out.println(England.this.name);
                System.out.println(InnerUsaTest2.this.name);
            }
        }
    }
}
