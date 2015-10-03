package org.powlab.jeye.scenario.data;

import java.awt.Frame;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Sample7<T, K, L extends Serializable> extends Frame {
    private static int a;
    private int b;
    private String k;
    public T t;

    public boolean if1(Integer reference0, int int0) {
        if (reference0 == null) {
            ++int0;
        }
        return false;
    }

//    public enum Status {
//        FIRST {
//
//
//        },
//        SECOND {
//            public String toString() {
//                return "";
//            }
//
//        };
//
//    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    public @interface Annotation {
        String value();

        String[] value1() default {"1", "2"};

        int integer() default 1;
    }

    interface Test$1<K, T extends Exception> {
        void method1();

        void method2() throws RuntimeException;

        <N> N method3() throws Exception;

        K method4();

        K[] method5(K[][] array0) throws T;
    }

//    public class Test$2 {
//        public class Test$3 {
//            public class Test$4 {
//            }
//        }
//
//        public class Test$5 {
//            public class Test$6 {
//            }
//        }
//    }
}