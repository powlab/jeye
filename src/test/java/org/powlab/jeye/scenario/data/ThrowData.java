package org.powlab.jeye.scenario.data;

public class ThrowData {

    public void mega() {
        throw new RuntimeException("Hello");
    }

    public int returnTest() {
        return 25;
    }

    public void returnTest2() {
        return;
    }

    public char toChar(int symbol) {
        return (char) symbol;
    }

    public boolean testZeroIf(int value) {
        return value == 0;
    }

    public boolean testNullIf(Integer value) {
        return value == null;
    }

    public boolean testRefIf(Integer value, Integer value1) {
        return value == value1;
    }

}
