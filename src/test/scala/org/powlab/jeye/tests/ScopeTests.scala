package org.powlab.jeye.tests

import org.powlab.jeye.tests.scope._
import org.powlab.jeye.decompile._

package scope {

  class ScopeTest1Test extends DecompileTestClass(classOf[ScopeTest1]) {}

  // TODO OK/FAIL: проблема определения типа переменной: Object вместо StructTest2,
  @org.junit.Ignore
  class ScopeTest10Test extends DecompileTestClass(classOf[ScopeTest10]) {}

  class ScopeTest11Test extends DecompileTestClass(classOf[ScopeTest11]) {}

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class ScopeTest2Test extends DecompileTestClass(classOf[ScopeTest2]) {}

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class ScopeTest3Test extends DecompileTestClass(classOf[ScopeTest3]) {}

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class ScopeTest4Test extends DecompileTestClass(classOf[ScopeTest4]) {}

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class ScopeTest5Test extends DecompileTestClass(classOf[ScopeTest5]) {}

  // TODO OK/FAIL: нет поддержки блоков {} - логически код декомпилируется правильно.
  @org.junit.Ignore
  class ScopeTest6Test extends DecompileTestClass(classOf[ScopeTest6[_]]) {}

  // TODO OK/FAIL: нет поддержки блоков {} - логически код декомпилируется правильно.
  @org.junit.Ignore
  class ScopeTest7Test extends DecompileTestClass(classOf[ScopeTest7[_]]) {}

  class ScopeTest8Test extends DecompileTestClass(classOf[ScopeTest8], true) {}

  class ScopeTest9Test extends DecompileTestClass(classOf[ScopeTest9]) {}
}