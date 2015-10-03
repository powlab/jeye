package org.powlab.jeye.tests.trycf;

import java.io.IOException;
import java.io.StringWriter;

public class TryTest4 {

    public void foo() throws IOException {
        try (final StringWriter sw = new StringWriter()) {
            sw.write("test");
        }

    }
}
