package org.powlab.jeye.scenario.data;

public class Sample8 {

    private void sep() {
    }

    private void call(boolean value) {
    }

    public boolean type1() {
        boolean value = false;
        sep();
        return value;
    }

    public boolean type2(boolean value) {
        value = true;
        sep();
        return value;
    }

    public Object type3() {
        boolean value = false;
        sep();
        return value;
    }

    public Object type4() {
        boolean value = true;
        sep();
        return value;
    }

    public void type5() {
        boolean value = false;
        sep();
        call(value);
    }

    public void type6() {
        boolean value = type1();
        sep();
        call(value);
    }

    public char type7(char symbol) {
        char value = 'a';
        sep();
        return value;
    }

    public void type8(char symbol) {
        type2(true);
    }

    public void type9() {
        type7('b');
    }

    public void type0() {
        type8('c');
    }

}
