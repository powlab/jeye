package org.powlab.jeye.tests.interfaces;

import java.util.HashMap;
import java.util.Map;



public interface InterfaceTestMembers {
    public final Map<String, Integer> z = new HashMap<String, Integer>();
    public int x = 4; // what does this even mean?  It's not final....
}
