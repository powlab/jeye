package org.powlab.jeye.tests

import org.powlab.jeye.tests.cond._
import org.powlab.jeye.decompile._

package cond {

  class CondJumpTest1Test extends DecompileTestClass(classOf[CondJumpTest1]) {}

  class CondJumpTest1aTest extends DecompileTestClass(classOf[CondJumpTest1a]) {}

  class CondJumpTest1bTest extends DecompileTestClass(classOf[CondJumpTest1b]) {}

  class CondJumpTest1cTest extends DecompileTestClass(classOf[CondJumpTest1c]) {}

  class CondJumpTest1dTest extends DecompileTestClass(classOf[CondJumpTest1d]) {}

  class CondJumpTest1eTest extends DecompileTestClass(classOf[CondJumpTest1e]) {}

  class CondJumpTest2Test extends DecompileTestClass(classOf[CondJumpTest2], true) {}

  class CondJumpTest2aTest extends DecompileTestClass(classOf[CondJumpTest2a], true) {}

  class CondJumpTest2bTest extends DecompileTestClass(classOf[CondJumpTest2b], true) {}

  class CondJumpTest2cTest extends DecompileTestClass(classOf[CondJumpTest2c]) {}

  class CondJumpTest3Test extends DecompileTestClass(classOf[CondJumpTest3]) {}

  class CondJumpTest3aTest extends DecompileTestClass(classOf[CondJumpTest3a], true) {}

  class CondJumpTest3bTest extends DecompileTestClass(classOf[CondJumpTest3b], true) {}

  class CondJumpTest3cTest extends DecompileTestClass(classOf[CondJumpTest3c], true) {}

  class CondJumpTest4Test extends DecompileTestClass(classOf[CondJumpTest4], true) {}

  class CondJumpTest5Test extends DecompileTestClass(classOf[CondJumpTest5]) {}

  class CondJumpTest6Test extends DecompileTestClass(classOf[CondJumpTest6]) {}

  class CondJumpTest7Test extends DecompileTestClass(classOf[CondJumpTest7]) {}

  class CondJumpTest8Test extends DecompileTestClass(classOf[CondJumpTest8]) {}

  //TODO OK: компилятор съедает байт-код
  @org.junit.Ignore
  class CondJumpTest9Test extends DecompileTestClass(classOf[CondJumpTest9]) {}

  class CondJumpTestInlineAssignTest extends DecompileTestClass(classOf[CondJumpTestInlineAssign]) {}

  class CondJumpTestInlineAssign1Test extends DecompileTestClass(classOf[CondJumpTestInlineAssign1]) {}

  //TODO OK: работает оптимизатор и еще можно улучшить
  @org.junit.Ignore
  class CondJumpTestSplitTest extends DecompileTestClass(classOf[CondJumpTestSplit]) {}

  class CondJumpTestSplit2Test extends DecompileTestClass(classOf[CondJumpTestSplit2]) {}

  class CondJumpTestSplit3Test extends DecompileTestClass(classOf[CondJumpTestSplit3], true) {}

  //TODO OK: байт код для boolean и int не различим. см CondJumpTestSplit5Test
  @org.junit.Ignore
  class CondJumpTestSplit4Test extends DecompileTestClass(classOf[CondJumpTestSplit4]) {}

  class CondJumpTestSplit5Test extends DecompileTestClass(classOf[CondJumpTestSplit5], true) {}

  class CondJumpTriopTest extends DecompileTestClass(classOf[CondJumpTriop]) {}

  class CondTest1Test extends DecompileTestClass(classOf[CondTest1], true) {}

  //TODO FAIL: проблеммы с патернами if конструкций,в том числе и тернарки, нужно серъезно подумать,тест то простой
  @org.junit.Ignore
  class CondTest2Test extends DecompileTestClass(classOf[CondTest2]) {}

  class CondTest3Test extends DecompileTestClass(classOf[CondTest3], true) {}

  class CondTest4Test extends DecompileTestClass(classOf[CondTest4], true) {}

  //TODO FAIL: проблеммы с патернами if конструкций,в том числе и тернарки, нужно серъезно подумать,тест то простой
  @org.junit.Ignore
  class CondTest5Test extends DecompileTestClass(classOf[CondTest5], true) {}

  //TODO FAIL: проблеммы с патернами if конструкций,в том числе и тернарки, нужно серъезно подумать,тест то простой
  @org.junit.Ignore
  class CondTest6Test extends DecompileTestClass(classOf[CondTest6], true) {}

  //TODO FAIL: проблеммы с патернами if конструкций,в том числе и тернарки, нужно серъезно подумать,тест то простой
  @org.junit.Ignore
  class CondTest7Test extends DecompileTestClass(classOf[CondTest7], true) {}

  class CondTest8Test extends DecompileTestClass(classOf[CondTest8], true) {}

  class CondTest9Test extends DecompileTestClass(classOf[CondTest9], true) {}

  class CondTest10Test extends DecompileTestClass(classOf[CondTest10], true) {}

  class CondTest11Test extends DecompileTestClass(classOf[CondTest11], true) {}

}