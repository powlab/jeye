package org.powlab.jeye.tests.exception;

public class ExceptionTestFinally22d {

    public String test(int x) {
        try {
            if (x == 0) {
                x = 1;
            }

            try {
                return "one";
            }
            finally {
                System.out.println("inner finally");
            }
        }
        finally {
            return ("outer finally");
        }
    }
}
