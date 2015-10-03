package org.powlab.jeye.tests.exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class ExceptionTest2 {

    void test1(String[] path) {
        try {
            File file = new File(path[0]);
            FileInputStream fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("File Not found");
            for (String s : path) {
                System.out.println(s);
            }
        }
    }


}
