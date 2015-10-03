package org.powlab.jeye.tests.arith;

public class ArithOpTest6 {
    static long nextStaticIdx;
    long nextIdx;

    public Long getNextIdent(long long0) {
        return new Long(nextIdx++);
    }

    public Long getPrevIdent(long long0) {
        return new Long(nextIdx--);
    }

    public long getNextIdent() {
        return nextIdx++;
    }

    public long getPrevIdent() {
        return nextIdx--;
    }

    public Long getNextIdentPre(long long0) {
        return new Long(++nextIdx);
    }

    public Long getPrevIdentPre(long long0) {
        return new Long(--nextIdx);
    }

    public long getNextIdentPre() {
        return ++nextIdx;
    }

    public long getPrevIdentPre() {
        return --nextIdx;
    }

    public Long getStaticNextIdent(long long0) {
        return new Long(nextStaticIdx++);
    }

    public Long getStaticPrevIdent(long long0) {
        return new Long(nextStaticIdx--);
    }

    public long getStaticNextIdent() {
        return nextStaticIdx++;
    }

    public long getStaticPrevIdent() {
        return nextStaticIdx--;
    }

    public Long getStaticNextIdentPre(long long0) {
        return new Long(++nextStaticIdx);
    }

    public Long getStaticPrevIdentPre(long long0) {
        return new Long(--nextStaticIdx);
    }

    public long getStaticNextIdentPre() {
        return ++nextStaticIdx;
    }

    public long getStaticPrevIdentPre() {
        return --nextStaticIdx;
    }
}
