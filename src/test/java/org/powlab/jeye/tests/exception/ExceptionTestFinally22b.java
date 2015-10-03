package org.powlab.jeye.tests.exception;

public class ExceptionTestFinally22b {

    public String test(final int x) {
        try {
            try {
                if (x==1) return "one";
            }
            finally {
                System.out.println("inner finally");
            }
        }
        finally {
            System.out.println("finally");
        }
        return "Done";
    }
}
