package org.powlab.jeye.scenario.data;

public class Sample1 {

    public boolean if1(Integer value, int k) {
        if (value == null) {
            k++;
        }
        return false;
    }

    public boolean if2(Integer value, int k) {
        if (value == null) {
            k++;
        } else {
            k--;
        }
        return false;
    }

    public boolean if3(Integer value, int k) {
        if (value == null) {
            k++;
        } else if (k > 0) {
            value = value + ++k + k++;
        }
        return false;
    }

    public boolean if4(Integer value, int k) {
        if (value == null) {
            k++;
        } else if (k > 0) {
            k--;
        } else {
            k = 7;
        }
        return false;
    }

    public boolean if5(Integer value, int k) {
        if (value == null || k > 10 || k < -1) {
            k++;
        }
        return false;
    }

    public boolean if6(Integer value, int k) {
        if (value == null && k > 10 && k < 100) {
            k++;
        }
        return false;
    }

    public boolean if7(Integer value, int k) {
        if (value == null) {
            return false;
        }
        k++;
        return false;
    }

    public int if8(Integer value, int k) {
        return k > 0 ? 1 : 2;
    }

    public int if0(Integer value, int k) {
        return 2 + (k > 0 || k < 10 ? 1 : 2);
    }

    public int if9(Integer value, int k) {
        return k > 0 || k < 10 ? 1 : 2;
    }

    public int ifq(Integer value, int k) {
        int l = k > 0 || k < 10 ? 1 : 2;
        return l + 1;
    }

    public boolean ifw(Integer value, int k) {
        if ((value == null && k > 10) || (value != null && k < 100)) {
            k++;
        }
        return false;
    }

    public boolean ife(Integer value, int k) {
        if ((value == null || k > 10) && (value != null || k < 100)) {
            k++;
        }
        return false;
    }

    public boolean ifr(Integer value, int k) {
        if ((value == null) ^ (k > 10)) {
            k++;
        }
        return false;
    }

    public boolean ift(Integer value, int k) {
        if (!((value == null) ^ (k > 10))) {
            k++;
        }
        return false;
    }

    public boolean ify(Integer value, int k) {
        if (value == null && k > 10 && k < 100) {
            k++;
        } else if (value != null && k < 100) {
            k--;
        }
        return false;
    }

    /*((int0 == 0 && int0 < 1) || (int0 > 2|| int2 < 3)) &&
    ((int0 > 4 || int1 < 5) && (int0 < 6 && int1 > 7)) ||
    ((int0 < 8 && int1 < 9) || (int0 < 10 || int1 < 11))*/

    public boolean ifu(int int0, int int1, int int2) {
        if (
                (
                 (
                         ((int0 == 0 && int0 < 1) || (int0 > 2|| int2 < 3)) &&
                         ((int0 > 4 || int1 < 5) && (int0 < 6 && int1 > 7)) ^
                         ((int0 < 8 && int1 < 9) || (int0 < 10 || int1 < 11))
                 ) ^
                 (int0 > 12)
                ) ||
                (int1 < 13)) {
            int0++;
        }
        return false;
    }

    public boolean ifi(int int0, int int1, int int2) {
        if (
                (((int0 > 0 || int1 < 1) || (int0 < 2 && int1 > 3))
                        &&
                  ((int1 > 4 || int0 < 5) && (int0 < 6 && int1 > 7)))
                ||
                  (((int2 > 8 || int0 < 9) || (int2 < 10 && int1 > 11))
                          &&
                  ((int2 > 12 || int0 < 13) && (int2 < 14 && int1 > 15)))
           ) {
            int0++;
        }
        return false;
    }

    public boolean ifo(int int0, int int1, int int2) {
        if (
                ((((int0 == 0) || (int1 == 0)) && int2 == 0) || (int0 != 2 && int1 !=2 && int2 !=2))
                || (int0 > int1 && int2 < int0)
                ) {
            int0++;
        }
        return false;
    }

    public boolean ifa(Integer value, int k) {
        if (value != null && k < 100) {
            k++;
        }
        return false;
    }

    public boolean ifs(Integer value, int k) {
        if (value != null || k < 100) {
            k++;
        }
        return false;
    }

    public boolean ifd(Integer value, int k) {
        if ((value != null || k < 100) ^ (value == null && k > 10 && k < 100)) {
            k++;
        }
        return false;
    }

    public boolean iff(Integer value, int k) {
        if ((value != null && k < 100) ^ (value == null || k > -1)) {
            k++;
        }
        return false;
    }

    public boolean ifg(Integer value, int k) {
        if (value != null ^ k != 7 ^ k != 8 ^ k < 100) {
            k++;
        }
        return true;
    }

    public boolean ifh(Integer value, int k) {
        if ((value != null ^ k != 7) ^ (k != 8 && k < 100)) {
            k++;
        }
        return true;
    }

    /** TODO here: var не декодируется как boolean */
    public boolean ifk(Integer value, int k) {
        boolean var = false;
        if (var) {
            k++;
        }
        return true;
    }

    public boolean ifl(boolean value, int k) {
        if (value) {
            k++;
        }
        return true;
    }

}

/**
 * TODO here:  занести на youtrack 2 БАГА
 * public class Frame:javassist.jar
 * 1) В методе merge не правильно определяется тип переменной int2 - должно быть boolean а не int
 * 2) В методе merge пропало выражение int3++, см инструкцию '97 iinc 3 1' - из-за этого выходит бесконечный цикл
 *
Входные параметры:
this
reference0
Runtime Opcodes метода:
00 iconst_0                  //Push int constant
01 istore_2                  //Store int <2> into local variable
02 iconst_0                  //Push int constant
03 istore_3                  //Store int <3> into local variable
04 iload_3                   //Load int from local variable
05 aload_0                   //Load reference from local variable
06 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
09 arraylength               //Get length of array
10 if_icmpge 0 93            //Branch if int comparison succeeds, param = 103
13 aload_0                   //Load reference from local variable
14 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
17 iload_3                   //Load int from local variable
18 aaload                    //Load reference from array
19 ifnull 0 55               //Branch if reference is null, param = 74
22 aload_0                   //Load reference from local variable
23 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
26 iload_3                   //Load int from local variable
27 aaload                    //Load reference from array
28 astore 4                  //Store reference into local variable
30 aload 4                   //Load reference from local variable
32 aload_1                   //Load reference from local variable
33 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
36 iload_3                   //Load int from local variable
37 aaload                    //Load reference from array
38 invokevirtual 0 15        //Invoke instance method; dispatch based on class, param = javassist.bytecode.analysis.Type.merge
41 astore 5                  //Store reference into local variable
43 aload_0                   //Load reference from local variable
44 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
47 iload_3                   //Load int from local variable
48 aload 5                   //Load reference from local variable
50 aastore                   //Store into reference array
51 aload 5                   //Load reference from local variable
53 aload 4                   //Load reference from local variable
55 invokevirtual 0 23        //Invoke instance method; dispatch based on class, param = javassist.bytecode.analysis.Type.equals
58 ifeq 0 11                 //Branch if int comparison with zero succeeds, param = 69
61 aload 5                   //Load reference from local variable
63 invokevirtual 0 24        //Invoke instance method; dispatch based on class, param = javassist.bytecode.analysis.Type.popChanged
66 ifeq 0 5                  //Branch if int comparison with zero succeeds, param = 71
69 iconst_1                  //Push int constant
70 istore_2                  //Store int <2> into local variable
71 goto 0 26                 //Branch always, param = 97
74 aload_1                   //Load reference from local variable
75 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
78 iload_3                   //Load int from local variable
79 aaload                    //Load reference from array
80 ifnull 0 17               //Branch if reference is null, param = 97
83 aload_0                   //Load reference from local variable
84 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
87 iload_3                   //Load int from local variable
88 aload_1                   //Load reference from local variable
89 getfield 0 3              //Fetch field from object, param = javassist.bytecode.analysis.Frame.locals
92 iload_3                   //Load int from local variable
93 aaload                    //Load reference from array
94 aastore                   //Store into reference array
95 iconst_1                  //Push int constant
96 istore_2                  //Store int <2> into local variable
97 iinc 3 1                  //Increment local variable by constant
100 goto 255 160             //Branch always, param = 4
103 iload_2                  //Load int from local variable
104 aload_0                  //Load reference from local variable
105 aload_1                  //Load reference from local variable
106 invokevirtual 0 25       //Invoke instance method; dispatch based on class, param = javassist.bytecode.analysis.Frame.mergeStack
109 ior                      //Boolean OR int
110 istore_2                 //Store int <2> into local variable
111 iload_2                  //Load int from local variable
112 ireturn                  //Return int from method
Exceptions:
no exceptions
[group] (112#1 [110 istore_2 | 111 iload_2 | 112 ireturn],org.powlab.jeye.decode.method.pattern.stream.ReturnStoreLoadStreamPattern@250970c1,112 ireturn), typer = return_store_load

    public boolean merge(Frame reference0) {
        int int2 = 0;
        int int3 = 0;
        while (int3 < locals.length) {
            if (locals[int3] != null) {
                Type reference3 = locals[int3];
                Type reference4 = reference3.merge(reference0.locals[int3]);
                locals[int3] = reference4;
                if (!reference4.equals(reference3) || reference4.popChanged()) {
                    int2 = 1;
                }
            } else             if (reference0.locals[int3] != null) {
                locals[int3] = reference0.locals[int3];
                int2 = 1;
            }
        }
        return (int2 | mergeStack(reference0));
    }
    */
