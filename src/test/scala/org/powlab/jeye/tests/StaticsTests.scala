package org.powlab.jeye.tests

import org.powlab.jeye.tests.statics._
import org.powlab.jeye.decompile._

package statics {

  // TODO OK/FAIL: проблема инициализации переменных в статическом блоке и в конструкторе
  @org.junit.Ignore
  class StaticInitTest1Test extends DecompileTestClass(classOf[StaticInitTest1]) {}

  // TODO FAIL: проблема инициализации переменных в статическом блоке и дженерики
  @org.junit.Ignore
  class StaticInitTest2Test extends DecompileTestClass(classOf[StaticInitTest2]) {}

  // TODO FAIL: проблема инициализации переменных в статическом блоке и дженерики
  @org.junit.Ignore
  class StaticInitTest3Test extends DecompileTestClass(classOf[StaticInitTest3]) {}

  // TODO FAIL: проблема в дженериках
  @org.junit.Ignore
  class StaticInitTest4Test extends DecompileTestClass(classOf[StaticInitTest4]) {}

  // TODO FAIL: не правильно декодируются внутренние классы и их имена
  @org.junit.Ignore
  class StaticInnerClassTest1Test extends DecompileTestClass(classOf[StaticInnerClassTest1]) {}

  class StaticTest1Test extends DecompileTestClass(classOf[StaticTest1[_]]) {}
}