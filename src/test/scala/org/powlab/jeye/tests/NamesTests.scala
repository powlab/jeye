package org.powlab.jeye.tests

import org.powlab.jeye.tests.names._
import org.powlab.jeye.decompile._

package names {

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class NameTest1Test extends DecompileTestClass(classOf[NameTest1]) {}

  class NameTest10Test extends DecompileTestClass(classOf[NameTest10], true) {}

  class NameTest11Test extends DecompileTestClass(classOf[NameTest11], true) {}

  class NameTest12Test extends DecompileTestClass(classOf[NameTest12], true) {}

  class NameTest13Test extends DecompileTestClass(classOf[NameTest13]) {}

  class NameTest13bTest extends DecompileTestClass(classOf[NameTest13b], true) {}

  class NameTest14Test extends DecompileTestClass(classOf[NameTest14]) {}

  // TODO FAIL: не правильно декодируются внутренние классы (private static)
  @org.junit.Ignore
  class NameTest15Test extends DecompileTestClass(classOf[NameTest15]) {}

  // TODO FAIL: не правильно декодируются внутренние классы (private static)
  @org.junit.Ignore
  class NameTest16Test extends DecompileTestClass(classOf[NameTest16]) {}

  // TODO OK/FAIL: проблема определения типа переменной: Object вместо Number,
  @org.junit.Ignore
  class NameTest17Test extends DecompileTestClass(classOf[NameTest17]) {}

  // TODO OK/FAIL: проблема определения типа переменной: Object вместо Number,
  @org.junit.Ignore
  class NameTest18Test extends DecompileTestClass(classOf[NameTest18]) {}

  // TODO OK/FAIL: проблема определения типа переменной: Object вместо Number,
  @org.junit.Ignore
  class NameTest19Test extends DecompileTestClass(classOf[NameTest19]) {}

  // TODO OK/FAIL: нет поддержки блоков {} - логически код декомпилируется правильно.
  @org.junit.Ignore
  class NameTest2Test extends DecompileTestClass(classOf[NameTest2]) {}

  // TODO OK/FAIL: проблема определения типа переменной: Object вместо Serializable,
  @org.junit.Ignore
  class NameTest20Test extends DecompileTestClass(classOf[NameTest20]) {}

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class NameTest21Test extends DecompileTestClass(classOf[NameTest21]) {}

  // TODO FAIL: нет поддержки блоков {} - вскрывает много проблем при их использовании.
  @org.junit.Ignore
  class NameTest3Test extends DecompileTestClass(classOf[NameTest3]) {}

  // TODO FAIL: нет поддержки блоков {} - вскрывает много проблем при их использовании.
  @org.junit.Ignore
  class NameTest4Test extends DecompileTestClass(classOf[NameTest4]) {}

  // TODO FAIL: нет поддержки блоков {} - вскрывает много проблем при их использовании.
  @org.junit.Ignore
  class NameTest5Test extends DecompileTestClass(classOf[NameTest5]) {}

  // TODO FAIL: нет поддержки блоков {} - вскрывает много проблем при их использовании.
  @org.junit.Ignore
  class NameTest6Test extends DecompileTestClass(classOf[NameTest6]) {}

  // TODO FAIL: нет поддержки блоков {} - вскрывает много проблем при их использовании.
  @org.junit.Ignore
  class NameTest7Test extends DecompileTestClass(classOf[NameTest7]) {}

  // TODO FAIL: нет поддержки блоков {} - вскрывает много проблем при их использовании.
  @org.junit.Ignore
  class NameTest8Test extends DecompileTestClass(classOf[NameTest8]) {}

  class NameTest9Test extends DecompileTestClass(classOf[NameTest9], true) {}
}