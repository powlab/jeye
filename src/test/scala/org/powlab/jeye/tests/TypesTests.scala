package org.powlab.jeye.tests

import org.powlab.jeye.tests.types._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

// Вопросы:
// TypeTest4Test, TypeTest5Test
// 1) Как правильно должна  ранслироваться конструкция '? extends Object' ()
// Варианты:
//  - '? extends Object'
//  - 'Object'
package types {

  class TypeTest1Test extends DecompileTestClass(classOf[TypeTest1]) {}

  class TypeTest10Test extends DecompileTestClass(classOf[TypeTest10]) {}

  class TypeTest11Test extends DecompileTestClass(classOf[TypeTest11]) {}

  //TODO here: необходимо доработать работу с дженериками
  @org.junit.Ignore
  class TypeTest12Test extends DecompileTestClass(classOf[TypeTest12]) {}

  //TODO OK: незначительные отличаи, возможны улучшения если правильно интерпритировать что использованть int/char тип
  @org.junit.Ignore
  class TypeTest13Test extends DecompileTestClass(classOf[TypeTest13]) {}

  //TODO OK: незначительные отличаи, возможны улучшения если правильно интерпритировать что использованть int/char тип
  @org.junit.Ignore
  class TypeTest14Test extends DecompileTestClass(classOf[TypeTest14]) {}

  //TODO OK/FAIL: еще требуется проработка дженериков+автобоксинг
  class TypeTest15Test extends DecompileTestClass(classOf[TypeTest15], false, ArrayBuffer(("returnl", "return(Integer)l"))) {}

  class TypeTest16Test extends DecompileTestClass(classOf[TypeTest16]) {}

  //TODO FAIL: внутренние классы и именование похожих с java.lang классов
  @org.junit.Ignore
  class TypeTest17Test extends DecompileTestClass(classOf[TypeTest17]) {}

  //TODO FAIL: дженерики
  @org.junit.Ignore
  class TypeTest18Test extends DecompileTestClass(classOf[TypeTest18]) {}

  //TODO FAIL: дженерики
  @org.junit.Ignore
  class TypeTest19Test extends DecompileTestClass(classOf[TypeTest19]) {}

  //TODO FAIL: дженерики
  @org.junit.Ignore
  class TypeTest19bTest extends DecompileTestClass(classOf[TypeTest19b]) {}

  //TODO OK: не возможно определить тип short для z
  class TypeTest1aTest extends DecompileTestClass(classOf[TypeTest1a], true, ArrayBuffer(("shortz", "intz"))) {}

  //TODO OK/FAIL: псевдо string +
  @org.junit.Ignore
  class TypeTest1bTest extends DecompileTestClass(classOf[TypeTest1b]) {}

  class TypeTest2Test extends DecompileTestClass(classOf[TypeTest2[_]]) {}

  //TODO FAIL: внутренние классы
  @org.junit.Ignore
  class TypeTest20Test extends DecompileTestClass(classOf[TypeTest20[_, _]]) {}

  class TypeTest21Test extends DecompileTestClass(classOf[TypeTest21]) {}

  class TypeTest22Test extends DecompileTestClass(classOf[TypeTest22]) {}

  class TypeTest23Test extends DecompileTestClass(classOf[TypeTest23]) {}

  class TypeTest24Test extends DecompileTestClass(classOf[TypeTest24]) {}

  class TypeTest25Test extends DecompileTestClass(classOf[TypeTest25]) {}

  //TODO FAIL: внутренние классы
  @org.junit.Ignore
  class TypeTest26Test extends DecompileTestClass(classOf[TypeTest26]) {}

  //TODO OK: все корректно, только по-другому
  class TypeTest27Test extends DecompileTestClass(classOf[TypeTest27], true, ArrayBuffer(("(short)s", "s"), ("i,i,(int)s", "i,i,(double)s"), ("(double)i,c,s", "(double)i,(int)c,s"))) {}

  class TypeTest28Test extends DecompileTestClass(classOf[TypeTest28]) {}

  class TypeTest28bTest extends DecompileTestClass(classOf[TypeTest28b]) {}

  class TypeTest29Test extends DecompileTestClass(classOf[TypeTest29]) {}

  class TypeTest3Test extends DecompileTestClass(classOf[TypeTest3[_]]) {}

  class TypeTest30Test extends DecompileTestClass(classOf[TypeTest30]) {}

  // TODO OK/FAIL: много мелких нюансов, восновном типы (char/int, boolean/int) дженерики и др
  @org.junit.Ignore
  class TypeTest31Test extends DecompileTestClass(classOf[TypeTest31]) {}

  // TODO OK/FAIL: требуется определять при математических операциях тип, если это char,
  //               то выражение '(char0 - 48)' должно выглядеть так '(char0 - '0')'
  @org.junit.Ignore
  class TypeTest32Test extends DecompileTestClass(classOf[TypeTest32]) {}

  // TODO OK/FAIL: дженерики
  @org.junit.Ignore
  class TypeTest33Test extends DecompileTestClass(classOf[TypeTest33[_]]) {}

  // TODO OK/FAIL: дженерики и varargs не работает для шаблонных методов
  @org.junit.Ignore
  class TypeTest34Test extends DecompileTestClass(classOf[TypeTest34]) {}

  // TODO OK: наш движок все упростил
  @org.junit.Ignore
  class TypeTest35Test extends DecompileTestClass(classOf[TypeTest35]) {}

  // TODO OK: наш движок все упростил
  class TypeTest36Test extends DecompileTestClass(classOf[TypeTest36], false, ArrayBuffer(("newInstance=C", "returnC"), ("returnnewInstance;", ""))) {}

  // TODO OK: похоже идет потеря информации о типах
  class TypeTest4Test extends DecompileTestClass(classOf[TypeTest4[_]], false, ArrayBuffer(("Map<String,?extendsObject>", "Map<String,Object>"), ("Map<String,?superObject>", "Map<String,Object>"))) {}

  // TODO OK: похоже идет потеря информации о типах
  class TypeTest5Test extends DecompileTestClass(classOf[TypeTest5[_]], false, ArrayBuffer(("?extends", ""), ("?super", ""))) {}

  class TypeTest6Test extends DecompileTestClass(classOf[TypeTest6]) {}

  // TODO OK: для двух разных тестов один и тотже байт код
  @org.junit.Ignore
  class TypeTest7Test extends DecompileTestClass(classOf[TypeTest7]) {}

  class TypeTest7aTest extends DecompileTestClass(classOf[TypeTest7a]) {}

  class TypeTest8Test extends DecompileTestClass(classOf[TypeTest8], true) {}

  class TypeTest9Test extends DecompileTestClass(classOf[TypeTest9], true) {}
}