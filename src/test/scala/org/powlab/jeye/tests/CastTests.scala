package org.powlab.jeye.tests

import org.powlab.jeye.tests.cast._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package cast {

  class CastTest1Test extends DecompileTestClass(classOf[CastTest1], true) {}

  class CastTest2Test extends DecompileTestClass(classOf[CastTest2], true) {}

  class CastTest2aTest extends DecompileTestClass(classOf[CastTest2a], true) {}

  class CastTest3Test extends DecompileTestClass(classOf[CastTest3], true) {}

  class CastTest4Test extends DecompileTestClass(classOf[CastTest4]) {}

  class CastTest5Test extends DecompileTestClass(classOf[CastTest5]) {}

  class CastTest6Test extends DecompileTestClass(classOf[CastTest6]) {}

  class CastTest6aTest extends DecompileTestClass(classOf[CastTest6a]) {}

  class CastTest7Test extends DecompileTestClass(classOf[CastTest7]) {}

  //TODO here: Тест не различим - компилятор сам кастует к long
  //class CastTest8Test extends DecompileTestClass(classOf[CastTest8]) {}

  class CastTest12Test extends DecompileTestClass(classOf[CastTest12]) {}

  class CastTest13Test extends DecompileTestClass(classOf[CastTest13]) {}

  class CastTest16Test extends DecompileTestClass(classOf[CastTest16]) {}

  class CastTest17Test extends DecompileTestClass(classOf[CastTest17]) {}

  //TODO OK: Декомпилирует ок, байт код генерится одинаковый
  @org.junit.Ignore
  class CastTest9Test extends DecompileTestClass(classOf[CastTest9]) {}

  //TODO fail: Не хватает боксинга int -> Number
  @org.junit.Ignore
  class CastTest10Test extends DecompileTestClass(classOf[CastTest10]) {}

  //TODO fail: final параметры метода еще не сделаны + раскрыть var args параметры
  @org.junit.Ignore
  class CastTest11Test extends DecompileTestClass(classOf[CastTest11]) {}

  class CastTest14Test extends DecompileTestClass(classOf[CastTest14], true) {}

  //TODO fail: не сделана инициализация статических полей класса
  @org.junit.Ignore
  class CastTest15Test extends DecompileTestClass(classOf[CastTest15]) {}

  //TODO fail: не определяется тип boolean для операции ^ 1
  @org.junit.Ignore
  class CastTest18Test extends DecompileTestClass(classOf[CastTest18]) {}

  class CastTest19Test extends DecompileTestClass(classOf[CastTest19]) {}

}