package org.powlab.jeye.tests

import org.powlab.jeye.tests.arith._
import org.powlab.jeye.decompile._

package arith {

  class ArithOpTest1Test extends DecompileTestClass(classOf[ArithOpTest1], true) {}

  class ArithOpTest2Test extends DecompileTestClass(classOf[ArithOpTest2], true) {}

  //TODO OK: компилятор сошел с ума и в 2 раза больше инструкций генерит, под eclipse все ок
  @org.junit.Ignore
  class ArithOpTest3Test extends DecompileTestClass(classOf[ArithOpTest3]) {}

  class ArithOpTest4Test extends DecompileTestClass(classOf[ArithOpTest4], true) {}

  class ArithOpTest5Test extends DecompileTestClass(classOf[ArithOpTest5]) {}

  class ArithOpTest6Test extends DecompileTestClass(classOf[ArithOpTest6]) {}

}