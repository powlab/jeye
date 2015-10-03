package org.powlab.jeye.tests.inner;


import java.util.Iterator;

import org.powlab.jeye.tests.support.NotNull;

public class InnerClassTest23_b {

        public void test(final boolean b) {
            abstract class MethodScopedIterable implements Iterable<String> {
                private final boolean y = b;

                @NotNull
                @Override
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                        @Override
                        public boolean hasNext() {
                            return MethodScopedIterable.this.y;
                        }

                        @Override
                        public String next() {
                            return null;
                        }

                        @Override
                        public void remove() {
                        }
                    };
                }
            }
        }

}
