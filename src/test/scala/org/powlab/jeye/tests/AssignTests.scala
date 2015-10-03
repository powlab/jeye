package org.powlab.jeye.tests

import org.powlab.jeye.tests.assign._
import org.powlab.jeye.decompile._

package assign {

  class AssignTest1Test extends DecompileTestClass(classOf[AssignTest1]) {}

  //TODO OK: добавить инициализацию
  class AssignTest2Test extends DecompileTestClass(classOf[AssignTest2]) {}

  //TODO OK: добавить инициализацию
  class AssignTest2aTest extends DecompileTestClass(classOf[AssignTest2a]) {}

  class AssignTest3Test extends DecompileTestClass(classOf[AssignTest3]) {}

  class AssignTest3aTest extends DecompileTestClass(classOf[AssignTest3a]) {}

  class AssignTest3bTest extends DecompileTestClass(classOf[AssignTest3b]) {}

  class AssignTest3cTest extends DecompileTestClass(classOf[AssignTest3c]) {}

  //TODO FAIL: проблемы с определением типа при множественном присваении родственных классов
  @org.junit.Ignore
  class AssignTest4Test extends DecompileTestClass(classOf[AssignTest4]) {}

  //TODO FAIL: проблемы с определением типа при множественном присваении родственных классов
  @org.junit.Ignore
  class AssignTest5Test extends DecompileTestClass(classOf[AssignTest5]) {}

  //TODO FAIL: проблемы с определением типа
  @org.junit.Ignore
  class AssignTest6Test extends DecompileTestClass(classOf[AssignTest6]) {}

  //TODO FAIL: проблемы с определением типа
  @org.junit.Ignore
  class AssignTest7Test extends DecompileTestClass(classOf[AssignTest7]) {}

  //TODO FAIL: проблемы с определением типа
  @org.junit.Ignore
  class AssignTest8Test extends DecompileTestClass(classOf[AssignTest8]) {}

  //TODO FAIL: проблемы с определением типа, почти
  @org.junit.Ignore
  class AssignTest9Test extends DecompileTestClass(classOf[AssignTest9]) {}

  class AssignTest10Test extends DecompileTestClass(classOf[AssignTest10]) {}

  class AssignTest11Test extends DecompileTestClass(classOf[AssignTest11]) {}

  class AssignTest12Test extends DecompileTestClass(classOf[AssignTest12]) {}

  class AssignTest12aTest extends DecompileTestClass(classOf[AssignTest12a]) {}

  class AssignTest12bTest extends DecompileTestClass(classOf[AssignTest12b], true) {}

  //TODO FAIL: проблемы с определением типа
  @org.junit.Ignore
  class AssignTest13Test extends DecompileTestClass(classOf[AssignTest13]) {}

  //TODO OK: байт код генерится одинаковый для конструкции i = i + 2/ i+= 2
  @org.junit.Ignore
  class AssignTest14Test extends DecompileTestClass(classOf[AssignTest14]) {}

  class AssignTest14aTest extends DecompileTestClass(classOf[AssignTest14a], true) {}

  class AssignTest14bTest extends DecompileTestClass(classOf[AssignTest14b], true) {}

  class AssignTest15Test extends DecompileTestClass(classOf[AssignTest15]) {}

}