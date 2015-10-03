package org.powlab.jeye.tests.exception;

import java.io.File;
import java.io.IOException;


public class ExceptionTest9 {

    void test1(String path) throws Exception {
        File.createTempFile(path, path);
    }

    void test2(String path) throws Exception {
        try {
            File.createTempFile(path, path);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    void test3(String path) throws Exception {
        try {
            File.createTempFile(path, path);
        } catch (IOException e) {
            throw e;
        }
    }
}
