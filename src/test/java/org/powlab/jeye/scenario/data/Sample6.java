package org.powlab.jeye.scenario.data;

public class Sample6 {

    public int if1(Integer value, int k) {
        return k > 0 ? 1 : 2;
    }

    public int if2(Integer value, int k) {
        return 2 + (k > 0 || k < 10 ? 1 : 2);
    }

    public int if3(Integer value, int k) {
        return k > 0 || k < 10 ? 1 : 2;
    }

    public int if4(Integer value, int k) {
        return k > 0 ^ k < 10 ? 1 : 2;
    }

    public int if5(Integer value, int k) {
        return 1 + (k > 0 ? 1 : 2) + 3 + (k < 5 ? 2 : 3);
    }

    public int if6(Integer value, int k) {
        return 1 + (k > 0 ? 1 : k < 2 ? 2 : 3) + 3 + (k < 5 ? 2 : 3);
    }

    public int if7(Integer value, int k) {
        int test = 1 + (k > 0 ? 1 : k < 2 ? 2 : 3) + 3 + (k < 5 ? 2 : 3);
        test++;
        return test;
    }

    public int if8(Integer value, int k) {
        int test = (k > 0 ? k < 2 ? 2 : 3 : 4);
        test++;
        return test;
    }

    public int if9(Integer value, int k) {
        int test = (k > 0 ? k < 2 ? 2 : 3 : k !=5 ? 7 : 9);
        test++;
        return test;
    }

    /**
     * TODO here: интересный случай
     * Всплыла проблема определения тернарного оператора
     * см переменную m
     */
    public boolean if0(Integer value, int k) {
        boolean p = k < 7;
        boolean m = k < 5 ? k < 0 : false;
        return (p && m) || k == 0 && value != null;
    }

    public boolean ifq(Integer value, int k) {
        int d = k == 0 ? k < 7 ? k > 3 ? 3 : 4 : k == 9 ? 8 : k == 6 ? 4: 5 : 7;
        return d == 7;
    }

    public boolean ifw(Integer value, int k) {
        int d = k == 0 ? k < 7 ? k > 3 ? 3 : 4 : k == 9 ? 8 : k == 6 ? 4: 5 : 7;
        return d == 7;
    }

    public boolean ife(long value, long k) {
        if (value > k) {
            return false;
        }
        return true;
    }

    public boolean ifr(long l1, long l2, float f1, float f2, double d1, double d2) {
        if (l1 > l2) {
            return false;
        }
        if (l1 >= l2) {
            return false;
        }
        if (f1 < f2) {
            return false;
        }
        if (f1 <= f2) {
            return false;
        }
        if (d1 == d2) {
            return false;
        }
        if (d1 != d2) {
            return false;
        }
        return true;
    }

    public static int ift(int k) {
        int d = k > 10 ? 2 : k < 6 ? 7 : 11;
        return d;
    }

    public static int ify(int k) {
        int d = k > 6 ? k < 10 ? 7 : 11 : 3;
        return d;
    }

 // это шедевр
    public void ifu(int j) {
        int i = 1 + (j < 3 ? (i = j + (j < 4 ? (i = 4) : (i = 2))) : (i = 1));
    }

    public void ifi(int j) {
        int i = 1+(j < 3 ? j=j+3 : 32);
    }

    public void ifo(int a) {
        if (a == 1) {
            System.out.println("One");
        } else if (a == 2 || a == 3) {
            System.out.println("2/3");
        } else if (a <= 5) {
            System.out.println("4/5");
        } else if (a == 6) {
            System.out.println("6");
        }
        System.out.println("Done");
    }

    public boolean ifp(boolean a, boolean b, boolean c) {
        System.out.println(a || (c ? a : b));
        return c;
    }

    public static void main(String[] args) {
        for (int i = 5; i < 12; i++) {
            System.out.println("(" + ift(i) + ", " + ify(i) + ")");
        }
    }


}
