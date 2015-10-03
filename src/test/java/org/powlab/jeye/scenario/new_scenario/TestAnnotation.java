package org.powlab.jeye.scenario.new_scenario;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TestAnnotation {

    String name();

    String type() default "string";

    Status status() default Status.A;

    Status[] statuses() default {Status.A, Status.B};

    int[] array1();

    Ann[] annotation();

    float floatValue() default 1.0f;

    public static enum Status {
        A,
        C,
        B
    }
}

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@interface Ann {

    String name() default "ann";

    int b() default 1;

    Class<?> k() default int.class;

}
