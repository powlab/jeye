package org.powlab.jeye.tests.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationTestAnnotation1 extends Annotation {
    String[] value();

    int fred();
}
