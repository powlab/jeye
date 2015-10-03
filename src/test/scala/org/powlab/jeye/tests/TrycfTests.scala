package org.powlab.jeye.tests

import org.powlab.jeye.tests.trycf._
import org.powlab.jeye.decompile._

package trycf {

  //TODO OK/FAIL: сливаются try/finally, но ход выполнения идентичен
  @org.junit.Ignore
  class TryTest1Test extends DecompileTestClass(classOf[TryTest1], true) {}

  class TryTest2Test extends DecompileTestClass(classOf[TryTest2], true) {}

  class TryTest3Test extends DecompileTestClass(classOf[TryTest3], true) {}

  //TODO FAIL: try-with-resources, надо разбираться, фигово декомпилируется
  @org.junit.Ignore
  class TryTest4Test extends DecompileTestClass(classOf[TryTest4]) {}

  class TryTest5Test extends DecompileTestClass(classOf[TryTest5], true) {}

  class TryTest6Test extends DecompileTestClass(classOf[TryTest6], true) {}

  //TODO OK/FAIL: разварачиваем if/else - получается лучше. Лишний блок от finally для eclipse
  @org.junit.Ignore
  class TryTestStrobel1Test extends DecompileTestClass(classOf[TryTestStrobel1], true) {}

  class DynamicCatchTest1Test extends DecompileTestClass(classOf[DynamicCatchTest1], true) {}
}