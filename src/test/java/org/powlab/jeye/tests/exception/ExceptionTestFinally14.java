package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally14 {

    int test1(int a) {
      bob : {
        try {
           if (a==1) break bob;
           if (a==2) return 0;
           if (a==3) break bob;
           if (a==4) return 1;
           if (a==5) break bob;
           if (a==6) return 9;
           if (a==7) break bob;
           if (a==8) return 2;
           if (a==9) break bob;
       } catch (Throwable t) {
           throw t;
       } finally {
           System.out.println("Fred");
       }
      }
       return 0;
    }


}
