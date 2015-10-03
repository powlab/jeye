package org.powlab.jeye.tests.assign;

public class AssignTest2 {
    // TODO here: вернуть инициализацию = 0;
    private static long sid;

    private final long id0;

    public AssignTest2() {
        id0 = sid++;
    }

    public AssignTest2(boolean b) {
        id0 = ++sid;
    }

}
