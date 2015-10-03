package org.powlab.jeye.tests.exception;



public abstract class ExceptionTest11 {

    public String test(boolean b, String in) {
        String classFile;
        if (b) {
            classFile = "TEST";
        } else {
            try {
                classFile = getName();
            } catch (RuntimeException e) {
                // We can't load the lambda target - we can't really make any assumptions about what it will do.
                return in;
            }
        }
        if (classFile == null) {
            return in;
        }
        return classFile;
    }

    abstract String getName();
}
