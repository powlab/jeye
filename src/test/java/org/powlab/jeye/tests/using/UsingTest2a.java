package org.powlab.jeye.tests.using;

import java.io.IOException;
import java.io.StringWriter;

public class UsingTest2a {
    public StringWriter testEnhancedTryEmpty() throws IOException {
        try (final StringWriter writer = new StringWriter()) {
            return writer;
        }
    }
}
