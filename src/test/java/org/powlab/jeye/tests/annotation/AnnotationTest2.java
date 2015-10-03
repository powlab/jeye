package org.powlab.jeye.tests.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationTest2
{
    String name() default "";

    Class<?> adapter() default Void.class;

    Class<?> adapter2() default void.class;

    Class<?> insertable() default float.class;

    Class<?> insertable2() default Float.class;

    boolean updatable() default true;
}
