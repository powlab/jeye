package org.powlab.jeye.tests.scope;

public class ScopeTest8 {
    public static void main(String... args) {
        int x;
        if (args.length == 0) {
            x = 3;
        } else {
            System.out.println((x = 4));
        }
    }
}
