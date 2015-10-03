package org.powlab.jeye.tests.abstracts;

import java.util.List;
import java.util.Map;

public abstract class AbstractTest2<T> {
    public abstract T create(List<String> reference0, Map<String, String> reference1);
}
