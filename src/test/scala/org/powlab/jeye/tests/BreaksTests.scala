package org.powlab.jeye.tests

import org.powlab.jeye.tests.breaks._
import org.powlab.jeye.decompile._

package breaks {

  //TODO ОК: Декодируется корректно, но не аналогично исходному коду
  @org.junit.Ignore
  class BreakTest1Test extends DecompileTestClass(classOf[BreakTest1]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду
  @org.junit.Ignore
  class BreakTest2Test extends DecompileTestClass(classOf[BreakTest2]) {}

  //TODO fail: Не умеем пока декомпилировать label без циклов
  @org.junit.Ignore
  class BreakTest3Test extends DecompileTestClass(classOf[BreakTest3]) {}

  class BreakTest4Test extends DecompileTestClass(classOf[BreakTest4], true) {}

  class BreakTest5Test extends DecompileTestClass(classOf[BreakTest5], true) {}

}