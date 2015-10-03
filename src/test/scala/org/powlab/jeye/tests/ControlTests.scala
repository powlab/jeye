package org.powlab.jeye.tests

import org.powlab.jeye.tests.control._
import org.powlab.jeye.decompile._

package control {

  //TODO FAIL: не хватает бесконечного цикла, доработать логику в CycleInfinityTypePattern
  @org.junit.Ignore
  class ControlFlowTest1Test extends DecompileTestClass(classOf[ControlFlowTest1], true) {}

  //TODO FAIL: не хватает бесконечного цикла, доработать логику в CycleInfinityTypePattern
  @org.junit.Ignore
  class ControlFlowTest1aTest extends DecompileTestClass(classOf[ControlFlowTest1a], true) {}

  //TODO OK: результат равнозначный получается, но меннее многословным!
  @org.junit.Ignore
  class ControlFlowTest2Test extends DecompileTestClass(classOf[ControlFlowTest2]) {}

  //TODO FAIL: не хватает слова continue внутри if блока
  @org.junit.Ignore
  class ControlFlowTest2aTest extends DecompileTestClass(classOf[ControlFlowTest2a]) {}

  //TODO FAIL: все плохо: семейство тестов ControlFlowTest2 нужно тюнить [CycleInfinityTypePattern]
  @org.junit.Ignore
  class ControlFlowTest2bTest extends DecompileTestClass(classOf[ControlFlowTest2b]) {}

  //TODO FAIL: не хватает бесконечного цикла, доработать логику в CycleInfinityTypePattern
  @org.junit.Ignore
  class ControlFlowTest3Test extends DecompileTestClass(classOf[ControlFlowTest3]) {}

  //TODO FAIL: не хватает бесконечного цикла, доработать логику в CycleInfinityTypePattern
  @org.junit.Ignore
  class ControlFlowTest4Test extends DecompileTestClass(classOf[ControlFlowTest4]) {}

  //TODO FAIL: не хватает бесконечного цикла, доработать логику в CycleInfinityTypePattern
  @org.junit.Ignore
  class ControlFlowTest4aTest extends DecompileTestClass(classOf[ControlFlowTest4a]) {}

  //TODO FAIL: не хватает бесконечного цикла, доработать логику в CycleInfinityTypePattern
  @org.junit.Ignore
  class ControlFlowTest5Test extends DecompileTestClass(classOf[ControlFlowTest5]) {}

  //TODO FAIL: бесконечный цикл определяется после try - наверное ошибка в tree.bind
  @org.junit.Ignore
  class ControlFlowTest6Test extends DecompileTestClass(classOf[ControlFlowTest6]) {}

  //TODO FAIL: не хватает бесконечного цикла, доработать логику в CycleInfinityTypePattern
  @org.junit.Ignore
  class ControlFlowTest7Test extends DecompileTestClass(classOf[ControlFlowTest7]) {}

  //TODO OK: наш движок оптимизировал выражение в лучшую сторону
  @org.junit.Ignore
  class ControlFlowTest8Test extends DecompileTestClass(classOf[ControlFlowTest8]) {}

  //TODO FAIL: первый while цикл определился как if, в целом не плохо.
  @org.junit.Ignore
  class ControlFlowTest9Test extends DecompileTestClass(classOf[ControlFlowTest9]) {}

}