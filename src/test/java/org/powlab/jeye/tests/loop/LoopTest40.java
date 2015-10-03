package org.powlab.jeye.tests.loop;


public class LoopTest40 {

    public void test(int [] xs)
    {
        int[] arr$ = xs;
        int len$ = arr$.length;
        int i$ = 0;
        do {
            if (i$ >= len$) {
                return;
            }
            int x = arr$[i$];
            System.out.println(x);
            ++i$;
        } while (true);
    }
}
