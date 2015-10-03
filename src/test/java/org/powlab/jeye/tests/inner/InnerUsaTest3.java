package org.powlab.jeye.tests.inner;


public class InnerUsaTest3 {
    private String name = "Detroit";

    public class England {
        private String name = "London";

        public class Ireland {
            private String name = "Dublin";

            public void print_names() {
                System.out.println(name);
                System.out.println(England.this.name);
                System.out.println(InnerUsaTest3.this.name);
            }
        }
    }
}
