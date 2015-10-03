package org.powlab.jeye.tests.control;

///**
// * http://www.program-transformation.org/Transform/
// * DecompilerControlFlowTestSource
// */
public class ControlFlowTest1a {
    // Внимание: методы foo и foo_old абсолютно равнозначны по codeflow
    // единственное что foo - менее многословен
    public int foo(int i, int j) {
        ++j;
        while (true) {
            System.out.println("fred");
            try {
                while (i < j) {
                    i = j++ / i;
                }
                break;
            } catch (RuntimeException re) {
                i = 10;
            }
        }
        return j;
    }

//    public int foo(int i, int j) {
//        j++;
//        while (true) {
//            System.out.println("fred");
//            try {
//                while (i < j) {
//                    i = j++ / i;
//                }
//            } catch (RuntimeException re) {
//                i = 10;
//                continue;
//            }
//            break;
//        }
//        return j;
//    }
}
