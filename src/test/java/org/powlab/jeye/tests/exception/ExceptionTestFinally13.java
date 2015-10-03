package org.powlab.jeye.tests.exception;


public class ExceptionTestFinally13 {

    int test1(int a) {
       try {
           if (a==1) return 9;
           if (a==2) return 0;
           if (a==3) return 2;
           if (a==4) return 1;
           if (a==5) return 0;
           if (a==6) return 9;
           if (a==7) return 0;
           if (a==8) return 2;
           if (a==9) return 1;
       } catch (Throwable t) {
           throw t;
       } finally {
           System.out.println("Fred");
       }
       return 0;
    }


}
