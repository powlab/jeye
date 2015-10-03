package org.powlab.jeye.tests.arith;

public class ArithOpTest5 {
    static int nextStaticIdx;
    int nextIdx;

    public Integer getNextIdent(int int0) {
        return new Integer(nextIdx++);
    }

    public Integer getPrevIdent(int int0) {
        return new Integer(nextIdx--);
    }

    public int getNextIdent() {
        return nextIdx++;
    }

    public Integer getPrevIdent() {
        return nextIdx--;
    }

    public Integer getNextIdentPre(int int0) {
        return new Integer(++nextIdx);
    }

    public Integer getPrevIdentPre(int int0) {
        return new Integer(--nextIdx);
    }

    public int getNextIdentPre() {
        return --nextIdx;
    }

    public Integer getPrevIdentPre() {
        return --nextIdx;
    }

    public Integer getStaticNextIdent(int int0) {
        return new Integer(nextStaticIdx++);
    }

    public Integer getStaticPrevIdent(int int0) {
        return new Integer(nextStaticIdx--);
    }

    public int getStaticNextIdent() {
        return nextStaticIdx++;
    }

    public Integer getStaticPrevIdent() {
        return nextStaticIdx--;
    }

    public Integer getStaticNextIdentPre(int int0) {
        return new Integer(++nextStaticIdx);
    }

    public Integer getStaticPrevIdentPre(int int0) {
        return new Integer(--nextStaticIdx);
    }

    public int getStaticNextIdentPre() {
        return --nextStaticIdx;
    }

    public Integer getStaticPrevIdentPre() {
        return --nextStaticIdx;
    }
}
