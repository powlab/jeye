package org.powlab.jeye.tests.annotation;


import org.powlab.jeye.tests.support.Nullable;

public class AnnotationTest1 {

    @Deprecated
    void foo(int x) {
    }

    @AnnotationTestAnnotation(value = {"fred", "jim"}, fred = 1)
//    @AnnotationTestAnnotation2("fred")
    void foo(int x, @Nullable Double y) {
        System.out.println("Foo!");
    }

    @AnnotationTestAnnotation({"fred", "jim"})
//    @AnnotationTestAnnotation2("fred")
    static void foo2(int x, @Nullable /*@AnnotationTestAnnotation2("pants") */ @Deprecated Double y) {
        System.out.println("Foo!");
    }

}
