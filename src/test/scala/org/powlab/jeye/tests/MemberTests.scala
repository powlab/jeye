package org.powlab.jeye.tests

import org.powlab.jeye.tests.member._
import org.powlab.jeye.decompile._

package member {

  class MemberTest1Test extends DecompileTestClass(classOf[MemberTest1]) {}

  //TODO: FAIL: дженерики и инициализация в конструкторе
  @org.junit.Ignore
  class MemberTest2Test extends DecompileTestClass(classOf[MemberTest2]) {}

  //TODO: FAIL: дженерики и инициализация в конструкторе
  @org.junit.Ignore
  class MemberTest3Test extends DecompileTestClass(classOf[MemberTest3]) {}

  //TODO: FAIL: дженерики и инициализация в конструкторе
  @org.junit.Ignore
  class MemberTest4Test extends DecompileTestClass(classOf[MemberTest4]) {}
}