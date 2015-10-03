package org.powlab.jeye.tests

import org.powlab.jeye.tests.precedence._
import org.powlab.jeye.decompile._

package precedence {

  // TODO OK: странный тест
  @org.junit.Ignore
  class PrecedenceTest1Test extends DecompileTestClass(classOf[PrecedenceTest1]) {}

  class PrecedenceTest2Test extends DecompileTestClass(classOf[PrecedenceTest2]) {}

  // TODO FAIL: условия/тернарки в условие плохо декодируются, нужно править патерны
  @org.junit.Ignore
  class PrecedenceTest3Test extends DecompileTestClass(classOf[PrecedenceTest3]) {}

  class PrecedenceTest4Test extends DecompileTestClass(classOf[PrecedenceTest4]) {}

  class PrecedenceTest5Test extends DecompileTestClass(classOf[PrecedenceTest5]) {}

  class PrecedenceTest6Test extends DecompileTestClass(classOf[PrecedenceTest6]) {}

  class PrecedenceTest7Test extends DecompileTestClass(classOf[PrecedenceTest7]) {}

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class PrecedenceTest8Test extends DecompileTestClass(classOf[PrecedenceTest8]) {}
}