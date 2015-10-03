package org.powlab.jeye.tests.exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class ExceptionTestFinally1 {

    void test1(String path) {
        try {
            int x = 3;
        } catch (Throwable t) {
            System.out.println("File Not found");
        } finally {
            System.out.println("Fred");
        }
    }


}
