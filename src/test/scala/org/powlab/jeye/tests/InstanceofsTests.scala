package org.powlab.jeye.tests

import org.powlab.jeye.tests.instanceofs._
import org.powlab.jeye.decompile._

package instanceofs {

  //TODO FAIL: не правильно декодируется внутринний класс и не хватает 'java.lang.' префикса
  @org.junit.Ignore
  class InstanceOfTest1Test extends DecompileTestClass(classOf[InstanceOfTest1], true) {}

  //TODO FAIL: не правильно декодируется внутринний класс и не хватает 'java.lang.' префикса
  @org.junit.Ignore
  class InstanceOfTest2Test extends DecompileTestClass(classOf[InstanceOfTest2], true) {}
}