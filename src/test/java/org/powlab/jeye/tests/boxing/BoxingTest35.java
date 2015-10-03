package org.powlab.jeye.tests.boxing;


public class BoxingTest35 {
    private String s1;
    private String s2;
    private String s3;
    private String s4;
    private String s5;
    private String s6;
    private String s7;
    private String s8;
    private String s9;
    private String s10;
    private String s11;
    private String s12;
    private String s13;
    private String s14;
    private String s15;
    private String s16;
    private String s17;
    private String s18;
    private String s19;

    private int addHashCode(int int0, Object reference0) {
        int int3 = int0 * 29;
        int int4 = reference0 == null ? 0 : reference0.hashCode();
        return int4 + int3;
    }

    public int hashCode() {
        return addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(addHashCode(0, s1),s2),s3),s4),s5),s6),s7),s8),s9),s10),s11), s12),s13),s14),s15),s16),s17),s18),s19);
    }

    public boolean equals(Object reference0) {
        return super.equals(reference0);
    }
}
