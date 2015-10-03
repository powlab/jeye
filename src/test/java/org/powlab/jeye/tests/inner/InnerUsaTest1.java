package org.powlab.jeye.tests.inner;


public class InnerUsaTest1 {
    public String name = "Detroit";

    public class England {
        public String name = "London";

        public class Ireland {
            public String name = "Dublin";

            public void print_names() {
                System.out.println(name);
            }
        }
    }
}
