package org.powlab.jeye.tests.assign;

public class AssignTest2a {
    // TODO here: вернуть инициализацию = 0;
    private static long sid;

    private final long id0;

    public AssignTest2a() {
        id0 = sid++;
        long y = sid;
    }

    public AssignTest2a(boolean b) {
        id0 = ++sid;
    }

}
