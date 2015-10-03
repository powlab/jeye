package org.powlab.jeye.tests.loop;

import java.util.Locale;


public class LoopTest46 {
    char [] value;

    public LoopTest46 toUpperCase(Locale locale) {

        int firstUpper;
        final int len = value.length;

        /* Now check if there are any characters that need to be changed. */
        scan: {
            for (firstUpper = 0 ; firstUpper < len; ) {
                char c = value[firstUpper];
                if ((c >= Character.MIN_HIGH_SURROGATE)
                        && (c <= Character.MAX_HIGH_SURROGATE)) {
                    int supplChar = (firstUpper);
                    if (supplChar != Character.toLowerCase(supplChar)) {
                        break scan;
                    }
                    firstUpper += Character.charCount(supplChar);
                } else {
                    if (c != Character.toLowerCase(c)) {
                        break scan;
                    }
                    firstUpper++;
                }
            }
            return this;
        }

        char[] result = new char[len];
        int resultOffset = 0;  /* result may grow, so i+resultOffset
                                    * is the write location in result */

            /* Just copy the first few lowerCase characters. */
        System.arraycopy(value, 0, result, 0, firstUpper);
        return null;
    }
}
