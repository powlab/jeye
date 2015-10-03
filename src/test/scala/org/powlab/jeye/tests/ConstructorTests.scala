package org.powlab.jeye.tests

import org.powlab.jeye.tests.constructor._
import org.powlab.jeye.decompile._
import scala.collection.mutable.ArrayBuffer

package constructor {

  class ConstructorChainTest1Test extends DecompileTestClass(classOf[ConstructorChainTest1]) {}

  class ConstructorChainTest2Test extends DecompileTestClass(classOf[ConstructorChainTest2]) {}

  class ConstructorChainTest3Test extends DecompileTestClass(classOf[ConstructorChainTest3]) {}

  //TODO FAIL: Некорректно определяется тип переменной a
  @org.junit.Ignore
  class ConstructorTest1Test extends DecompileTestClass(classOf[ConstructorTest1]) {}

  //TODO OK/FAIL: Не определяется final у аргумента метода
  class ConstructorTest2Test extends DecompileTestClass(classOf[ConstructorTest2], false, ArrayBuffer(("final", ""))) {}

  //TODO OK/FAIL: Не определяется final у аргумента метода
  class ConstructorTest3Test extends DecompileTestClass(classOf[ConstructorTest3], false, ArrayBuffer(("final", ""))) {}

  class ConstructorTest4Test extends DecompileTestClass(classOf[ConstructorTest4]) {}

  //TODO OK: переменная инициализируется в конструкторе, итог один и тотже
  @org.junit.Ignore
  class ConstructorTest5Test extends DecompileTestClass(classOf[ConstructorTest5]) {}

  //TODO OK: переменная инициализируется в конструкторах, итог один и тотже
  @org.junit.Ignore
  class ConstructorTest6Test extends DecompileTestClass(classOf[ConstructorTest6]) {}

}