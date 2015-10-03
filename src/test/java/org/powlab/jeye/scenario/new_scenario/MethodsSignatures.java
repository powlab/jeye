package org.powlab.jeye.scenario.new_scenario;

import java.io.IOException;
import java.io.Serializable;

public interface MethodsSignatures<K extends Exception> {

    void method();

    int method1();

    void method2(Long reference0, int int0, Object reference1) throws Exception;

    <T> T method3(T reference0, int int0, Object reference1) throws K, Exception;

    <T, R extends Comparable<T> & Serializable> T method4(R reference0, Integer[] array0, Object reference1, Long reference2, K[] array1) throws K, Exception;

    <T, R extends Comparable<T> & Serializable> T method5(R reference0, Integer[] array0, Object reference1, Long reference2, K[] array1) throws IOException, IllegalArgumentException;

    <T> T method6(long long0, int int0, Object reference0);

    @TestAnnotation(name = "name string", status = TestAnnotation.Status.A, array1 = {},
            annotation = {@Ann(name = "hello"), @Ann(), @Ann(k = TestAnnotation.Status[].class), @Ann(name = "hello"),
                    @Ann(b = 4), @Ann(k = Short.class)}, floatValue = 2.0f,
            statuses = {TestAnnotation.Status.A, TestAnnotation.Status.B, TestAnnotation.Status.C})
    void method7(Long reference0, int int0, Object reference1) throws Exception;

}
