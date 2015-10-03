package org.powlab.jeye.scenario.data;


//import javassist.bytecode.BadBytecode;

// Бесконечный цикл:
// Метод eqParamTypes класса javassist.bytecode.Descriptor
// CtConstructor.removeConsCall - bad
// addBodyMethod - классный пример для do/while!!!
// insertGap2w: CodeIterator - не правильно циклы обрабатываются
public class SingleMethod {

//    private static int sb = 5;
//    private static String sk = "11";
//    private int fb = 7;
//    private String fk = "13";
    public String cycley(Integer reic, int counter, Long longData) {
        return "A" + counter;
    }


//    public void _cycley(Integer reference0, int int0) {
//        int int2 = int0++
//                + (reference0 = Integer.valueOf(reference0.intValue() + 1)).intValue()
//                + (reference0 = Integer.valueOf(reference0.intValue() + 1)).intValue()
//                + (reference0 = Integer.valueOf(reference0.intValue() + 1));
//        System.out.println(reference0);
//        System.out.println(int2++ + ++int2);
//    }

}

// МЕГА ТЕСТ!!!
// public int test0(String reference0) {
// boolean test = false;
// if (reference0.charAt(0) == 'A' == test) {
// System.out.println("1");
// }
// return 1;
// }
//

// TODO here: нужно причесать бусконечный for
// public static boolean eqParamTypes(String desc1, String desc2) {
// if (desc1.charAt(0) != '(')
// return false;
//
// for (int i = 0; true; ++i) {
// char c = desc1.charAt(i);
// if (c != desc2.charAt(i))
// return false;
//
// if (c == ')')
// return true;
// }
// }
