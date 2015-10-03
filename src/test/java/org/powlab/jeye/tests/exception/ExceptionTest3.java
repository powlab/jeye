package org.powlab.jeye.tests.exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class ExceptionTest3 {

    String x() {
        return null;
    }

    void test1(String[] path) {
        String s = x();
        try {
            String b = x();
            try {
                System.out.println("S1" + s.length());

            } catch (NullPointerException e) {
                System.out.println("NPE1 " + b.length());
            }
        } catch (NullPointerException e) {
            System.out.println("NPE2");
        }
    }


}
