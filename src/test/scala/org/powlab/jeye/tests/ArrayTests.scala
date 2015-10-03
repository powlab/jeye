package org.powlab.jeye.tests

import org.powlab.jeye.tests.array._
import org.powlab.jeye.decompile._

package array {

  class ArrayTest1Test extends DecompileTestClass(classOf[ArrayTest1]) {}

  class ArrayTest2Test extends DecompileTestClass(classOf[ArrayTest2]) {}

  class ArrayTest3Test extends DecompileTestClass(classOf[ArrayTest3], true) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду
  @org.junit.Ignore
  class ArrayTest3aTest extends DecompileTestClass(classOf[ArrayTest3a]) {}

  class ArrayTest3bTest extends DecompileTestClass(classOf[ArrayTest3b], true) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду
  @org.junit.Ignore
  class ArrayTest3cTest extends DecompileTestClass(classOf[ArrayTest3c]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду
  @org.junit.Ignore
  class ArrayTest3dTest extends DecompileTestClass(classOf[ArrayTest3d]) {}

  //TODO OK: Декодируется корректно, но не аналогично исходному коду
  @org.junit.Ignore
  class ArrayTest3eTest extends DecompileTestClass(classOf[ArrayTest3e]) {}

  class ArrayTest3fTest extends DecompileTestClass(classOf[ArrayTest3f], true) {}

  class ArrayTest4Test extends DecompileTestClass(classOf[ArrayTest4]) {}

  class ArrayTest4aTest extends DecompileTestClass(classOf[ArrayTest4a]) {}

  class ArrayTest4bTest extends DecompileTestClass(classOf[ArrayTest4b]) {}

  //TODO OK/FAIL: но можно и лучше - не определяется Object[] array9, определяется как String[]
  @org.junit.Ignore
  class ArrayTest4cTest extends DecompileTestClass(classOf[ArrayTest4c]) {}

  //TODO OK/FAIL: но можно и лучше - не определяется Object[] array3, определяется как String[]
  @org.junit.Ignore
  class ArrayTest4dTest extends DecompileTestClass(classOf[ArrayTest4d]) {}

  //TODO OK/FAIL: но можно и лучше - не определяется Object[] array3, определяется как String[]
  @org.junit.Ignore
  class ArrayTest4eTest extends DecompileTestClass(classOf[ArrayTest4e]) {}

  //TODO OK/FAIL: но можно и лучше - не определяется Object[] array3, определяется как RuntimeException[]
  @org.junit.Ignore
  class ArrayTest4fTest extends DecompileTestClass(classOf[ArrayTest4f]) {}

  class ArrayTest5Test extends DecompileTestClass(classOf[ArrayTest5]) {}

  class ArrayTest6Test extends DecompileTestClass(classOf[ArrayTest6]) {}

  //TODO FAIL: Идет потеря информации, никак не поправить...
  @org.junit.Ignore
  class ArrayTest7Test extends DecompileTestClass(classOf[ArrayTest7]) {}

  class ArrayTest8Test extends DecompileTestClass(classOf[ArrayTest8]) {}

  class ArrayTest9Test extends DecompileTestClass(classOf[ArrayTest9], true) {}

  //TODO FAIL: 2 проблемы, некорректно определяется тип переменной и некорректно определяются модификаторы для внутренних классов
  @org.junit.Ignore
  class ArrayTest10Test extends DecompileTestClass(classOf[ArrayTest10]) {}

  //TODO FAIL: 2 проблемы, некорректно определяется тип переменной и некорректно определяются модификаторы для внутренних классов
  @org.junit.Ignore
  class ArrayTest10aTest extends DecompileTestClass(classOf[ArrayTest10a]) {}

  //TODO FAIL: 2 проблемы, некорректно определяется тип переменной и некорректно определяются модификаторы для внутренних классов
  @org.junit.Ignore
  class ArrayTest10bTest extends DecompileTestClass(classOf[ArrayTest10b]) {}

  //TODO FAIL: 2 проблемы, некорректно определяется тип переменной и некорректно определяются модификаторы для внутренних классов
  @org.junit.Ignore
  class ArrayTest10cTest extends DecompileTestClass(classOf[ArrayTest10c]) {}

  //TODO OK/FAIL: не красиво, статический блок инициализации должен уйти.
  @org.junit.Ignore
  class ArrayTest11Test extends DecompileTestClass(classOf[ArrayTest11]) {}

  //TODO OK/FAIL: не красиво, статический блок инициализации должен уйти.
  @org.junit.Ignore
  class ArrayTest12Test extends DecompileTestClass(classOf[ArrayTest12]) {}

  //TODO FAIL: bug - сложный тест, не корректно декомпилируется тернарка если идет инициализация массива
  @org.junit.Ignore
  class ArrayTest13Test extends DecompileTestClass(classOf[ArrayTest13]) {}

  class ArrayTest14Test extends DecompileTestClass(classOf[ArrayTest14]) {}

  //TODO OK/FAIL: не красиво, статический блок инициализации должен уйти.
  @org.junit.Ignore
  class ArrayTest15Test extends DecompileTestClass(classOf[ArrayTest15]) {}

}