package org.powlab.jeye.tests.other;

import java.util.ArrayList;

public class OverrideGenericBridge extends ArrayList<String>
{
    public String[] toArray(final String[] a) {
        final String[] array = super.toArray(a);
        array[2] = "wat";
        return array;
    }
}
