package org.powlab.jeye.tests

import org.powlab.jeye.tests.switches._
import org.powlab.jeye.decompile._

package switches {

  class SwitchTest1Test extends DecompileTestClass(classOf[SwitchTest1], true) {}

  class SwitchTest7Test extends DecompileTestClass(classOf[SwitchTest7], true) {}

  class SwitchTest10Test extends DecompileTestClass(classOf[SwitchTest10], true) {}

  class SwitchTest11Test extends DecompileTestClass(classOf[SwitchTest11], true) {}

  class SwitchTest12Test extends DecompileTestClass(classOf[SwitchTest12], true) {}

  // TODO FAIL: нужно писать трансформер по enum'ам, если он есть в класспасе
  @org.junit.Ignore
  class SwitchTest2Test extends DecompileTestClass(classOf[SwitchTest2]) {}

  // TODO FAIL: нужно писать трансформер по enum'ам, если он есть в класспасе и переходы
  @org.junit.Ignore
  class SwitchTest3Test extends DecompileTestClass(classOf[SwitchTest3]) {}

  // TODO FAIL: нужно писать трансформер по string (switch по string)
  @org.junit.Ignore
  class SwitchTest4Test extends DecompileTestClass(classOf[SwitchTest4]) {}

  // TODO FAIL: нужно писать трансформер по string (switch по string)
  @org.junit.Ignore
  class SwitchTest4aTest extends DecompileTestClass(classOf[SwitchTest4a]) {}

  // TODO FAIL: нужно писать трансформер по enum'ам, если он есть в класспасе
  @org.junit.Ignore
  class SwitchTest5Test extends DecompileTestClass(classOf[SwitchTest5]) {}

  // TODO FAIL: нужно писать трансформер по enum'ам, если он есть в класспасе
  @org.junit.Ignore
  class SwitchTest6Test extends DecompileTestClass(classOf[SwitchTest6]) {}

  class SwitchTest8Test extends DecompileTestClass(classOf[SwitchTest8], true) {}

  // TODO FAIL: нужно писать трансформер по enum'ам, если он есть в класспасе
  @org.junit.Ignore
  class SwitchTest9Test extends DecompileTestClass(classOf[SwitchTest9]) {}
}