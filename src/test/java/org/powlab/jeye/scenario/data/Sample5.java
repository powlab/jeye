package org.powlab.jeye.scenario.data;

import java.util.List;

public class Sample5 {

    public boolean cycle1(Integer value, int k) {
        int i = 0;
        while (i < 10) {
            i++;
        }
        return false;
    }

    public boolean cycle2() {
        for (int i = 0; i < 10; i++) {
            System.out.println("1");
        }
        return false;
    }

    public boolean cycle3(int k) {
        for (int i = 0; i < 10 && k != 0; i++) {
            System.out.println("1");
        }
        return false;
    }

    /**
     *  00 iconst_0                  //Push int constant
        01 istore_2                  //Store int <2> into local variable
        02 goto 0 23                 //Branch always, param = 25
        05 iload_2                   //Load int from local variable
        06 bipush 7                  //Push byte, param = 7
        08 if_icmpne 0 6             //Branch if int comparison succeeds, param = 14
        11 goto 0 20                 //Branch always, param = 31
        14 getstatic 0 24            //Get static field from class, param = java.lang.System.out
        17 ldc 30                    //Push item from run-time constant pool, param = 1}
        19 invokevirtual 0 32        //Invoke instance method; dispatch based on class, param = java.io.PrintStream.println
        22 iinc 2 1                  //Increment local variable by constant
        25 iload_2                   //Load int from local variable
        26 bipush 10                 //Push byte, param = 10
        28 if_icmplt 255 233         //Branch if int comparison succeeds, param = 5
        31 iconst_0                  //Push int constant
        32 ireturn                   //Return int from method
     * @param k
     * @return
     */
    public boolean cycle4(int k) {
        for (int i = 0; i < 10; i++) {
            if (i == 7) {
                break;
            }
            System.out.println("1");
        }
        return false;
    }

    /**
     *  00 iconst_0                  //Push int constant
     *  01 istore_2                  //Store int <2> into local variable
     *  02 goto 0 23                 //Branch always, param = 25
     *  05 iload_2                   //Load int from local variable
     *  06 bipush 7                  //Push byte, param = 7
     *  08 if_icmpne 0 6             //Branch if int comparison succeeds, param = 14
     *  11 goto 0 11                 //Branch always, param = 22
     *  14 getstatic 0 24            //Get static field from class, param = java.lang.System.out
     *  17 ldc 30                    //Push item from run-time constant pool, param = 1}
     *  19 invokevirtual 0 32        //Invoke instance method; dispatch based on class, param = java.io.PrintStream.println
     *  22 iinc 2 1                  //Increment local variable by constant
     *  25 iload_2                   //Load int from local variable
     *  26 bipush 10                 //Push byte, param = 10
     *  28 if_icmplt 255 233         //Branch if int comparison succeeds, param = 5
     *  31 iconst_0                  //Push int constant
     *  32 ireturn                   //Return int from method
     * @param k
     * @return
     */
    public boolean cycle5(int k) {
        for (int i = 0; i < 10; i++) {
            if (i == 7) {
                continue;
            }
            System.out.println("1");
        }
        return false;
    }

    public boolean cycle6() {
        for (int i = 0; i < 10; i++) {
            for (int k = 0; k < 10; k++) {
                System.out.println("1");
            }
        }
        return false;
    }

    public boolean cycle7() {
        for (int i = 0; i < 10; i++) {
            for (int k = 0; k < 10; k++) {
                if (i == k) {
                    System.out.println("1");
                }
            }
        }
        return false;
    }

    /**
     *  00 iconst_0                  //Push int constant
     *  01 istore_1                  //Store int <1> into local variable
     *  02 goto 0 36                 //Branch always, param = 38
     *  05 iconst_0                  //Push int constant
     *  06 istore_2                  //Store int <2> into local variable
     *  07 goto 0 22                 //Branch always, param = 29
     *  10 iload_1                   //Load int from local variable
     *  11 iload_2                   //Load int from local variable
     *  12 if_icmpne 0 6             //Branch if int comparison succeeds, param = 18
     *  15 goto 0 20                 //Branch always, param = 35
     *  18 getstatic 0 24            //Get static field from class, param = java.lang.System.out
     *  21 ldc 30                    //Push item from run-time constant pool, param = 1}
     *  23 invokevirtual 0 32        //Invoke instance method; dispatch based on class, param = java.io.PrintStream.println
     *  26 iinc 2 1                  //Increment local variable by constant
     *  29 iload_2                   //Load int from local variable
     *  30 bipush 10                 //Push byte, param = 10
     *  32 if_icmplt 255 234         //Branch if int comparison succeeds, param = 10
     *  35 iinc 1 1                  //Increment local variable by constant
     *  38 iload_1                   //Load int from local variable
     *  39 bipush 10                 //Push byte, param = 10
     *  41 if_icmplt 255 220         //Branch if int comparison succeeds, param = 5
     *  44 iconst_0                  //Push int constant
     *  45 ireturn                   //Return int from method
     * @return
     */
    public boolean cycle8() {
        label1: for (int i = 0; i < 10; i++) {
            for (int k = 0; k < 10; k++) {
                if (i == k) {
                    continue label1;
                }
                System.out.println("1");
            }
        }
        return false;
    }

    public boolean counter(int k) {
        return true;
    }

    public boolean cycle9(int k) {
        while (counter(k)) {
            System.out.println("1");
            if (k == 8) {
                continue;
            }
            if (k == 5) {
                break;
            }
            boolean l = counter(7);
            if (l) {
                System.out.println("2");
            }
        }
        return false;
    }

    // TODO here: N1. Вскрывает проблему обхода дерева (см. описание в коде)
    // TODO here: N2. if (boolean0 != 0) а должно быть if (boolean0)
    public boolean cycle0(int k) {
        while (counter(k)) {
            System.out.println("1");
            if (k == 8) {
                System.out.println("8");
                if (k > 3) {
                    continue;
                }
                k++;
            }
            if (k == 5) {
                break;
            }
            boolean l = counter(7);
            if (l) {
                System.out.println("2");
            }
        }
        return false;
    }

    public boolean cycleq(int k) {
        while (counter(k)) {
            System.out.println("1");
            if (k == 8) {
                System.out.println("8");
                if (k > 3) {
                    continue;
                }
                System.out.println("after continue");
            }
            if (k == 5) {
                break;
            }
            boolean l = counter(7);
            if (l) {
                System.out.println("2");
            }
            if (k == 0) {
                System.out.println("0");
            } else {
                System.out.println("any");
            }
            k++;
        }
        return false;
    }

    public boolean cyclew(int k) {
        for (int int1 = 0; int1 < 10; int1++) {
            k++;
            if (int1 == 7) {
                continue;
            }
            System.out.println(k);
        }
        return false;
    }

    public boolean cyclee(int k) {
        label1:
        for (int int1 = 0; int1 < 10; int1++) {
            k++;
            for (int int2 = 0; int2 < 10; int2++) {
                k++;
                if (int1 == 7) {
                    break label1;
                }
                System.out.println(k);
            }
        }
        return false;
    }

    public boolean cycler(int k) {
        label1:
        while (counter(k)) {
            k++;
            for (int int2 = 0; int2 < 10; int2++) {
                k++;
                if (int2 == 7) {
                    continue label1;
                }
                System.out.println(k++);
            }
        }
        return false;
    }

    public boolean cyclet(int k) {
        while (counter(k)) {
            if (k < 7) {
                k++;
                continue;
            }
            if (k < 17) {
                break;
            }
            return false;
        }
        return true;
    }

    /**
     * TODO here: декомпилируется не праильно, не хватает слова break
     */
    public boolean cycley(int k) {
        while (counter(k)) {
            if (k < 7) {
                k++;
                continue;
            }
            if (k < 17) {
                break;
            }
            break;
        }
        return true;
    }

    public boolean cycleu(int k) {
        while (counter(k)) {
            if (k < 7) {
                k++;
                continue;
            }
            if (k < 17) {
                break;
            }
            k+=2;
            continue;
        }
        return true;
    }

    /**
     *  00 goto 0 20                 //Branch always, param = 20
     *  03 iload_1                   //Load int from local variable
     *  04 bipush 7                  //Push byte, param = 7
     *  06 if_icmpne 0 6             //Branch if int comparison succeeds, param = 12
     *  09 goto 0 11                 //Branch always, param = 20
     *  12 getstatic 0 24            //Get static field from class, param = java.lang.System.out
     *  15 ldc 30                    //Push item from run-time constant pool, param = 1}
     *  17 invokevirtual 0 32        //Invoke instance method; dispatch based on class, param = java.io.PrintStream.println
     *  20 aload_0                   //Load reference from local variable
     *  21 iload_1                   //Load int from local variable
     *  22 invokevirtual 0 47        //Invoke instance method; dispatch based on class, param = org.powlab.jeye.scenario.data.Sample5.counter
     *  25 ifne 255 234              //Branch if int comparison with zero succeeds, param = 3
     *  28 iconst_0                  //Push int constant
     *  29 ireturn                   //Return int from method
     * @param k
     * @return
     */
    public void cyclei(boolean done) {
        while (true) {
            System.out.println("done: " + done);
            if (done) {
                System.out.println("3");
                break;
            }
            System.out.println("twice");
        }
        System.out.println("4");
    }

    public boolean cycleo(int k) {
        while (counter(k)) {
            if (k == 7) {
                continue;
            }
            System.out.println("1");
        }
        return false;
    }

    boolean testA(int int0, int int1) {
        return int0<int1;
    }

    boolean testB(int int0, int int1) {
        return int0<int1;
    }

    boolean testC(int int0, int int1) {
        return int0<int1;
    }

    public int cyclep(List<Integer> reference0, List<Integer> reference1) {
        labelA: for (Integer reference6 : reference0) {
            labelB: for (Integer reference9 : reference1) {
                System.out.println("A");
                if (reference9 == reference6)
                    break labelA;
                System.out.println("B");
                if (reference9 > reference6)
                    break labelB;
                System.out.println("C");
            }
        }
        return 1;
    }

    public int cyclea(List<Integer> reference0, List<Integer> reference1) {
        int int1 = 1;
        do {
            if (int1 > 200) {
                break;
            }
            System.out.println(int1);
            if (int1 % 5 == 0) {
                ++int1;
            } else {
                int1+=5;
            }
        } while (true);
        return 1;
    }

    public int cycles(int a, int b) {
        boolean x = true;
        a: do {
            System.out.println("A");
            b : do {
                System.out.println("B");
                c : do {
                    System.out.println("C");
                    if (testA(a,b)) break a;
                    if (testB(a,b)) continue b;
                    if (testC(a,b)) break b;
                    break c;
                } while (x);
                return 3;
            } while (x);
            System.out.println("Leaving b");
        } while (x);
        return 1;
    }

    public int cycled(int i, int j) {
        while (true) {
            // System.out.println("b");
            if (i++ < 5) {
                System.out.println("a");
            } else if (i < 10) {
                // System.out.println("F");
                continue;
            } else {
                continue;
            }
            // System.out.println("Fred");
            break;
        }
        return j;
    }

    public void functionWhichMightThrow() {
    }

    public boolean test() {
        return false;
    }

    public void cyclef()  {
        do {
            do {
                try {
                    functionWhichMightThrow();
                } catch (Exception e) {
                    break;
                }
            } while (test());
            System.out.println("A");
        } while (true);
    }

}
