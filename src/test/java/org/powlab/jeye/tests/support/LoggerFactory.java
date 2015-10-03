package org.powlab.jeye.tests.support;

public class LoggerFactory {
    public static <T> Logger create(Class<T> clazz) {
        return new Logger();
    }
}
