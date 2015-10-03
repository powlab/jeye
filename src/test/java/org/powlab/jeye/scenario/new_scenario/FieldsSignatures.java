package org.powlab.jeye.scenario.new_scenario;

public class FieldsSignatures<A, B, C, D, E, F> implements TestInterface<A, B, C, D, E, F> {

    private int first;

    @TestAnnotation(name = "name string", status = TestAnnotation.Status.A, array1 = {},
            annotation = {@Ann(name = "hello"), @Ann(), @Ann(k = TestAnnotation.Status[].class), @Ann(name = "hello"),
                    @Ann(b = 4), @Ann(k = Short.class)}, floatValue = 2.0f,
            statuses = {TestAnnotation.Status.A, TestAnnotation.Status.B, TestAnnotation.Status.C})
    private static boolean b;

    static final float c = 10.0f;

    public static final String d = "1";

    public final String d1 = "123";

    protected volatile transient int k;

    protected TestInterface<A[], B[], TestInterface<A, B, C, D, E, F>, Object, E, F> clazz;

}

