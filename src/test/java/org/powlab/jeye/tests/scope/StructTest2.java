package org.powlab.jeye.tests.scope;

public class StructTest2 {
    public StructTest1 x;

    void a() {
        this.x = new StructTest1();
        this.x.a = "TesT";
        this.x.b = 4;
    }
}
