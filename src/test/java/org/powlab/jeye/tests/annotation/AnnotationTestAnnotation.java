package org.powlab.jeye.tests.annotation;

public @interface AnnotationTestAnnotation {
    String[] value();

    int fred() default 3;
}
