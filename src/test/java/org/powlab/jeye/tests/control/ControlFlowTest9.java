package org.powlab.jeye.tests.control;

import java.util.regex.Matcher;

public class ControlFlowTest9 {
    private Node next;
    private Node atom;
    private int cmax;

    boolean match0(Matcher matcher, int i, int j, CharSequence seq) {
        int backLimit = j;
        while (atom.match(matcher, i, seq)) {
            // k is the length of this match
            int k = matcher.toString().length() - i;
            // Zero length match
            if (k == 0) {
                break;
            }
            // Move up index and number matched
            i = matcher.toString().length(); // TEST CONVERT
            j++;
            // We are greedy so match as many as we can
            while (j < cmax) {
                if (!atom.match(matcher, i, seq)) {
                    break;
                }
                if (i + k != matcher.toString().length()) {
                    if (match0(matcher, matcher.toString().length(), j + 1, seq)) {
                        return true;
                    }
                    break;
                }
                i += k;
                j++;
            }
            // Handle backing off if match fails
            while (j >= backLimit) {
                if (next.match(matcher, i, seq)) {
                    return true;
                }
                i -= k;
                j--;
            }
            return false;
        }
        return next.match(matcher, i, seq);
    }

    private static class Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return true;
        }
    }
}
