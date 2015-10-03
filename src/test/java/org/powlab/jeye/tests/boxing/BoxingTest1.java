package org.powlab.jeye.tests.boxing;

public class BoxingTest1 {
    private void a(Integer reference0) {
        a(reference0);
        b(reference0);
        c(reference0);
    }

    private void b(int int0) {
        a(int0);
        b(int0);
        c(int0);
    }

    private void c(double double0) {
        c(double0);
        d(double0);
    }

    private void d(Double reference0) {
        c(reference0);
        d(reference0);
    }

    private void e(Short reference0) {
        b(reference0);
        c(reference0);
        e(reference0);
        f(reference0);
    }

    private void f(short short0) {
        b(short0);
        c(short0);
        e(short0);
        f(short0);
    }


}
